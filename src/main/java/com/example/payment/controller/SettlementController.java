package com.example.payment.controller;

import com.example.payment.entity.Settlement;
import com.example.payment.service.SettlementService;
import com.example.payment.dto.SettlementResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping("/{settlementReference}/process")
    public ResponseEntity<Settlement> processSettlement(@PathVariable String settlementReference) {

        Settlement settlement = settlementService.processSettlement(settlementReference);

        return ResponseEntity.ok(settlementService.processSettlement(settlementReference));
    }
}
