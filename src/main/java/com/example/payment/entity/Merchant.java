package com.example.payment.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "merchants")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Merchant {
    public Merchant(String name, String email, MerchantStatus status, LocalDateTime createdAt) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private MerchantStatus status;

    private LocalDateTime createdAt;
    @Column(length = 500)
    private String webhookUrl;

    @Column(length = 100)
    private String webhookSecret;
}


