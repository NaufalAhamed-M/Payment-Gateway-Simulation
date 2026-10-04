package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.payment.entity.PaymentAttempt;
import com.example.payment.entity.PaymentAttemptStatus;
import com.example.payment.entity.PaymentStatus;
import com.example.payment.exception.MaxRetryExceededException;
import com.example.payment.processor.PaymentProcessor;
import com.example.payment.processor.PaymentProcessorResult;
import com.example.payment.repository.PaymentAttemptRepository;
import com.example.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentResolutionService {

    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final PaymentProcessor paymentProcessor;

    private static final int MAX_ATTEMPTS = 3;

    public PaymentResolutionService(
            PaymentRepository paymentRepository,
            PaymentAttemptRepository paymentAttemptRepository,
            PaymentProcessor paymentProcessor) {

        this.paymentRepository = paymentRepository;
        this.paymentAttemptRepository = paymentAttemptRepository;
        this.paymentProcessor = paymentProcessor;
    }

    @Transactional
    public Payment resolvePayment(String paymentReference) {

        Payment payment = paymentRepository
                .findByPaymentReference(paymentReference)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.UNKNOWN) {
            throw new RuntimeException(
                    "Payment cannot be resolved from status: "
                            + payment.getStatus()
            );
        }

        PaymentAttempt latestAttempt = paymentAttemptRepository
                .findTopByPaymentIdOrderByAttemptNumberDesc(payment.getId())
                .orElseThrow(() ->
                        new RuntimeException("Payment attempt not found"));

        PaymentProcessorResult result =
                paymentProcessor.checkStatus(latestAttempt);

        if (result.getStatus() == PaymentAttemptStatus.SUCCESS) {

            latestAttempt.setStatus(PaymentAttemptStatus.SUCCESS);
            latestAttempt.setProcessorReference(
                    result.getProcessorReference()
            );
            latestAttempt.setFailureReason(null);
            latestAttempt.setCompletedAt(LocalDateTime.now());

            paymentAttemptRepository.save(latestAttempt);

            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            return payment;
        }

        if (result.getStatus() == PaymentAttemptStatus.NOT_FOUND) {

            if (latestAttempt.getAttemptNumber() >= MAX_ATTEMPTS) {
                throw new MaxRetryExceededException(
                        "Maximum payment processing attempts exceeded"
                );
            }

            PaymentAttempt retryAttempt = new PaymentAttempt();

            retryAttempt.setPayment(payment);
            retryAttempt.setAttemptNumber(
                    latestAttempt.getAttemptNumber() + 1
            );
            retryAttempt.setStatus(PaymentAttemptStatus.PROCESSING);
            retryAttempt.setStartedAt(LocalDateTime.now());

            paymentAttemptRepository.save(retryAttempt);

            PaymentProcessorResult retryResult =
                    paymentProcessor.process(retryAttempt);

            retryAttempt.setStatus(retryResult.getStatus());
            retryAttempt.setProcessorReference(
                    retryResult.getProcessorReference()
            );
            retryAttempt.setFailureReason(
                    retryResult.getFailureReason()
            );
            retryAttempt.setCompletedAt(LocalDateTime.now());

            paymentAttemptRepository.save(retryAttempt);

            updatePaymentStatus(payment, retryResult);

            paymentRepository.save(payment);

            return payment;
        }

        return payment;
    }

    private void updatePaymentStatus(Payment payment, PaymentProcessorResult result) {
        if (result.getStatus() == PaymentAttemptStatus.SUCCESS) {
            payment.setStatus(PaymentStatus.SUCCESS);
        } else if (result.getStatus() == PaymentAttemptStatus.FAILED) {
            payment.setStatus(PaymentStatus.FAILED);
        } else if (result.getStatus() == PaymentAttemptStatus.TIMEOUT) {
            payment.setStatus(PaymentStatus.UNKNOWN);
        }
    }
}

