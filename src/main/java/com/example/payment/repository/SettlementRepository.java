package com.example.payment.repository;

import com.example.payment.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    Optional<Settlement> findBySettlementReference(
            String settlementReference
    );

    Optional<Settlement> findByPaymentId(Long paymentId);
}
