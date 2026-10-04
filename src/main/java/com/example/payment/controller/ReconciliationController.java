package com.example.payment.controller;

import com.example.payment.entity.ReconciliationRecord;
import com.example.payment.service.ReconciliationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @PostMapping("/{settlementReference}")
    public ResponseEntity<ReconciliationRecord> reconcile(@PathVariable String settlementReference) {

        ReconciliationRecord record = reconciliationService.reconcile(settlementReference);

        return ResponseEntity.ok(reconciliationService.reconcile(settlementReference));
    }
}
