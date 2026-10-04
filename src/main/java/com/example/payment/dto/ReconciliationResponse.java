package com.example.payment.dto;

import com.example.payment.entity.ReconciliationRecord;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ReconciliationResponse {

    private final String reconciliationReference;
    private final String settlementReference;
    private final BigDecimal expectedAmount;
    private final BigDecimal actualAmount;
    private final String status;
    private final LocalDateTime checkedAt;
    private final String mismatchReason;

    public ReconciliationResponse(ReconciliationRecord record) {
        this.reconciliationReference = record.getReconciliationReference();
        this.settlementReference =
                record.getSettlement().getSettlementReference();
        this.expectedAmount = record.getExpectedAmount();
        this.actualAmount = record.getActualAmount();
        this.status = record.getStatus().name();
        this.checkedAt = record.getCheckedAt();
        this.mismatchReason = record.getMismatchReason();
    }
}
