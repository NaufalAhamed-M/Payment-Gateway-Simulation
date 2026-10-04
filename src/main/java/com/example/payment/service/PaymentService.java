package com.example.payment.service;

import com.example.payment.dto.CreatePaymentRequest;
import com.example.payment.dto.PaymentCreationResult;
import com.example.payment.entity.*;
import com.example.payment.exception.IdempotencyConflictException;
import com.example.payment.repository.MerchantRepository;
import com.example.payment.repository.PaymentRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MerchantRepository merchantRepository;

    public PaymentService(PaymentRepository paymentRepository, MerchantRepository merchantRepository) {
        this.paymentRepository = paymentRepository;
        this.merchantRepository = merchantRepository;
    }

    public void validateMerchant(Long merchantId) {

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Merchant not found")
                );

        if (merchant.getStatus() != MerchantStatus.ACTIVE) {
            throw new RuntimeException("Merchant is inactive");
        }
    }

    public Optional<Payment> findExistingPayment(Long merchantId, String idempotencyKey) {
        return paymentRepository.findByMerchantIdAndIdempotencyKey(merchantId, idempotencyKey);
    }

    private boolean isSamePaymentRequest(Payment existingPayment, CreatePaymentRequest request) {
        return existingPayment.getAmount().compareTo(request.getAmount()) == 0
                && existingPayment.getCurrency().equals(request.getCurrency())
                && existingPayment.getPaymentMethod().name()
                .equals(request.getPaymentMethod());
    }

    private Payment handleIdempotency(CreatePaymentRequest request) {
        Optional<Payment> existingPayment =
                findExistingPayment(Long.valueOf(request.getMerchantId()), request.getIdempotencyKey()
                );

        if (existingPayment.isEmpty()) {
            return null;
        }

        Payment payment = existingPayment.get();

        if (isSamePaymentRequest(payment, request)) {
            return payment;
        }
        throw new IdempotencyConflictException("Idempotency key already used with different payment details");
    }

    @Transactional
    public PaymentCreationResult createPayment(CreatePaymentRequest request) {

        Long merchantId = Long.valueOf(request.getMerchantId());
        validateMerchant(merchantId);
        Payment existingPayment = handleIdempotency(request);

        if (existingPayment != null) {
            return new PaymentCreationResult(existingPayment, false);
        }
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        if (request.getCurrency() == null || !request.getCurrency().matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Currency must be a 3-letter uppercase code");
        }

        PaymentMethod paymentMethod;

        try {
            paymentMethod = PaymentMethod.valueOf(
                    request.getPaymentMethod().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unsupported payment method"
            );
        }

        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                merchant,
                request.getAmount(),
                request.getCurrency(),
                paymentMethod,
                PaymentStatus.CREATED,
                request.getIdempotencyKey(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Payment savedPayment = paymentRepository.save(payment);
        return new PaymentCreationResult(savedPayment, true);
    }
}
