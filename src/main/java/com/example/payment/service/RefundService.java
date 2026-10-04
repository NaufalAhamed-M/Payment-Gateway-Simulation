package com.example.payment.service;

import com.example.payment.dto.CreateRefundRequest;
import com.example.payment.entity.Payment;
import com.example.payment.entity.PaymentStatus;
import com.example.payment.entity.Refund;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.repository.RefundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;

    public RefundService(
            RefundRepository refundRepository,
            PaymentRepository paymentRepository) {

        this.refundRepository = refundRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Refund createRefund(CreateRefundRequest request) {

        Payment payment = paymentRepository
                .findByPaymentReference(request.getPaymentReference())
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException(
                    "Only successful payments can be refunded"
            );
        }

        BigDecimal refundAmount = request.getAmount();

        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new RuntimeException(
                    "Refund amount cannot exceed payment amount"
            );
        }

        Refund refund = new Refund(payment, refundAmount);

        refund.setStatus(
                com.example.payment.entity.RefundStatus.SUCCESS
        );

        refund.setCompletedAt(
                java.time.LocalDateTime.now()
        );
        return refundRepository.save(refund);
    }
}
