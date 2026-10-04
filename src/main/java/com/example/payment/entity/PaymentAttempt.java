package com.example.payment.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_attempts")
@Getter
@Setter
@NoArgsConstructor
public class PaymentAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false)
    private Integer attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentAttemptStatus status;

    private String processorReference;

    private String failureReason;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;
}
