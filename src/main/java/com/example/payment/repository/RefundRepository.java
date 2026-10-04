package com.example.payment.repository;

import com.example.payment.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    Optional<Refund> findByRefundReference(String refundReference);
}
