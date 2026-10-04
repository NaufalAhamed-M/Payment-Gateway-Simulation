package com.example.payment.service;

import com.example.payment.entity.*;
import com.example.payment.processor.PaymentProcessor;
import com.example.payment.processor.PaymentProcessorResult;
import com.example.payment.repository.PaymentAttemptRepository;
import com.example.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentProcessingService {

    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final PaymentProcessor paymentProcessor;
    private final WebhookService webhookService;
    private final AuditLogService auditLogService;
    private final SettlementService settlementService;

    public PaymentProcessingService(
            PaymentRepository paymentRepository,
            PaymentAttemptRepository paymentAttemptRepository,
            PaymentProcessor paymentProcessor,
            WebhookService webhookService,
            AuditLogService auditLogService,
            SettlementService settlementService
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentAttemptRepository = paymentAttemptRepository;
        this.paymentProcessor = paymentProcessor;
        this.webhookService = webhookService;
        this.auditLogService = auditLogService;
        this.settlementService = settlementService;
    }

    @Transactional
    public Payment startProcessing(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found")
                );

        if (payment.getStatus() != PaymentStatus.CREATED) {
            throw new RuntimeException(
                    "Payment cannot be processed from status: " + payment.getStatus()
            );
        }
        payment.setStatus(PaymentStatus.PROCESSING);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment startProcessingByReference(String paymentReference) {

        Payment payment = paymentRepository
                .findByPaymentReference(paymentReference)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found")
                );

        if (payment.getStatus() != PaymentStatus.CREATED) {
            throw new RuntimeException(
                    "Payment cannot be processed from status: "
                            + payment.getStatus()
            );
        }

        PaymentAttempt attempt = new PaymentAttempt();

        attempt.setPayment(payment);
        attempt.setAttemptNumber(1);
        attempt.setStatus(PaymentAttemptStatus.CREATED);
        attempt.setStartedAt(LocalDateTime.now());

        paymentAttemptRepository.save(attempt);

        attempt.setStatus(PaymentAttemptStatus.PROCESSING);

        payment.setStatus(PaymentStatus.PROCESSING);

        paymentRepository.save(payment);

        auditLogService.log(
                AuditAction.PAYMENT_PROCESSING_STARTED,
                "PAYMENT",
                payment.getPaymentReference(),
                "Payment processing started"
        );

        PaymentProcessorResult result = paymentProcessor.process(attempt);

        attempt.setStatus(result.getStatus());
        attempt.setProcessorReference(result.getProcessorReference());
        attempt.setFailureReason(result.getFailureReason());
        attempt.setCompletedAt(LocalDateTime.now());

        paymentAttemptRepository.save(attempt);

        if (result.getStatus() == PaymentAttemptStatus.SUCCESS) {
            payment.setStatus(PaymentStatus.SUCCESS);

        } else if (result.getStatus() == PaymentAttemptStatus.FAILED) {
            payment.setStatus(PaymentStatus.FAILED);

        } else if (result.getStatus() == PaymentAttemptStatus.TIMEOUT) {
            payment.setStatus(PaymentStatus.UNKNOWN);
        }

        paymentRepository.save(payment);
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            settlementService.createSettlement(payment);
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS) {

            auditLogService.log(
                    AuditAction.PAYMENT_SUCCESS,
                    "PAYMENT",
                    payment.getPaymentReference(),
                    "Payment processed successfully"
            );

        } else if (payment.getStatus() == PaymentStatus.FAILED) {

            auditLogService.log(
                    AuditAction.PAYMENT_FAILED,
                    "PAYMENT",
                    payment.getPaymentReference(),
                    "Payment processing failed"
            );

        } else if (payment.getStatus() == PaymentStatus.UNKNOWN) {

            auditLogService.log(
                    AuditAction.PAYMENT_UNKNOWN,
                    "PAYMENT",
                    payment.getPaymentReference(),
                    "Payment outcome is unknown"
            );
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS ||
                payment.getStatus() == PaymentStatus.FAILED) {

            webhookService.createPaymentWebhook(payment);
        }
        return payment;
    }
}
