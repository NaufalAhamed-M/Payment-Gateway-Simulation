package com.example.payment.repository;

import com.example.payment.entity.ReconciliationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReconciliationRecordRepository extends JpaRepository<ReconciliationRecord, Long> {

    Optional<ReconciliationRecord> findBySettlementId(Long settlementId);
}
