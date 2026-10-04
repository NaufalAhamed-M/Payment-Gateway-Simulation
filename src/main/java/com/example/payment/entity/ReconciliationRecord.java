package com.example.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reconciliation_records")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReconciliationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reconciliationReference;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false, unique = true)
    private Settlement settlement;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal expectedAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal actualAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReconciliationStatus status;

    @Column(nullable = false)
    private LocalDateTime checkedAt;

    private String mismatchReason;

    public ReconciliationRecord(
            Settlement settlement,
            BigDecimal expectedAmount,
            BigDecimal actualAmount
    ) {
        this.reconciliationReference = UUID.randomUUID().toString();
        this.settlement = settlement;
        this.expectedAmount = expectedAmount;
        this.actualAmount = actualAmount;
        this.checkedAt = LocalDateTime.now();

        if (expectedAmount.compareTo(actualAmount) == 0) {
            this.status = ReconciliationStatus.MATCHED;
        } else {
            this.status = ReconciliationStatus.MISMATCHED;
            this.mismatchReason =
                    "Expected amount does not match actual amount";
        }
    }
}
