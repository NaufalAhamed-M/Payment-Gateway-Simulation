package com.example.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private String entityReference;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public AuditLog(AuditAction action, String entityType, String entityReference, String description) {
        this.action = action;
        this.entityType = entityType;
        this.entityReference = entityReference;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
}
