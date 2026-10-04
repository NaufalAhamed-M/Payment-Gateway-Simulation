package com.example.payment.service;

import com.example.payment.entity.ReconciliationRecord;
import com.example.payment.entity.Settlement;
import com.example.payment.repository.ReconciliationRecordRepository;
import com.example.payment.repository.SettlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReconciliationService {

    private final SettlementRepository settlementRepository;
    private final ReconciliationRecordRepository reconciliationRepository;

    public ReconciliationService(
            SettlementRepository settlementRepository,
            ReconciliationRecordRepository reconciliationRepository
    ) {
        this.settlementRepository = settlementRepository;
        this.reconciliationRepository = reconciliationRepository;
    }

    @Transactional
    public ReconciliationRecord reconcile(
            String settlementReference
    ) {

        Settlement settlement = settlementRepository
                .findBySettlementReference(settlementReference)
                .orElseThrow(() ->
                        new RuntimeException("Settlement not found"));

        if (settlement.getStatus() !=
                com.example.payment.entity.SettlementStatus.SETTLED) {

            throw new RuntimeException(
                    "Only settled transactions can be reconciled"
            );
        }

        if (reconciliationRepository
                .findBySettlementId(settlement.getId())
                .isPresent()) {

            throw new RuntimeException(
                    "Settlement already reconciled"
            );
        }

        ReconciliationRecord record =
                new ReconciliationRecord(
                        settlement,
                        settlement.getPayment().getAmount(),
                        settlement.getGrossAmount()
                );

        return reconciliationRepository.save(record);
    }
}
