package com.example.payment.service;

import com.example.payment.entity.AuditAction;
import com.example.payment.entity.AuditLog;
import com.example.payment.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(AuditAction action, String entityType, String entityReference, String description) {
        AuditLog auditLog = new AuditLog(
                action,
                entityType,
                entityReference,
                description
        );
        auditLogRepository.save(auditLog);
    }
}
