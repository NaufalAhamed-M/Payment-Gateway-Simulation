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
@Table(name = "refunds")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String refundReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    public Refund(
            Payment payment,
            BigDecimal amount) {

        this.refundReference = UUID.randomUUID().toString();
        this.payment = payment;
        this.amount = amount;
        this.status = RefundStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }
}
