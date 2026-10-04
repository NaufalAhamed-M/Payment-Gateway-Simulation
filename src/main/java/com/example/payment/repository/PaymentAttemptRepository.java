package com.example.payment.repository;

import com.example.payment.entity.PaymentAttempt;
import com.example.payment.entity.PaymentAttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, Long> {
    Optional<PaymentAttempt> findTopByPaymentIdOrderByAttemptNumberDesc(
            Long paymentId
    );
}
