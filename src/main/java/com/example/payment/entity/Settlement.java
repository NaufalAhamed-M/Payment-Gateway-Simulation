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
@Table(name = "settlements")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String settlementReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal grossAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal gatewayFee;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal netAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime settledAt;

    public Settlement(
            Merchant merchant,
            Payment payment,
            BigDecimal grossAmount,
            BigDecimal gatewayFee,
            BigDecimal netAmount
    ) {
        this.settlementReference = UUID.randomUUID().toString();
        this.merchant = merchant;
        this.payment = payment;
        this.grossAmount = grossAmount;
        this.gatewayFee = gatewayFee;
        this.netAmount = netAmount;
        this.status = SettlementStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
