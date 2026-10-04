package com.example.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_merchant_idempotency", columnNames = {"merchant_id", "idempotency_key"})
                })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    public Payment(
            String paymentReference,
            Merchant merchant,
            BigDecimal amount,
            String currency,
            PaymentMethod paymentMethod,
            PaymentStatus status,
            String idempotencyKey,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.paymentReference = paymentReference;
        this.merchant = merchant;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentReference;

    @Column(nullable = false)
    private String idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}