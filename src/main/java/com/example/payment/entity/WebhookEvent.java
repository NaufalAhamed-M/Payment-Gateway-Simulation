package com.example.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhook_events")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String eventReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookEventStatus status;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deliveredAt;

    public WebhookEvent(
            Merchant merchant,
            Payment payment,
            String eventType,
            String payload
    ) {
        this.eventReference = UUID.randomUUID().toString();
        this.merchant = merchant;
        this.payment = payment;
        this.eventType = eventType;
        this.payload = payload;
        this.status = WebhookEventStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now();
    }
}
