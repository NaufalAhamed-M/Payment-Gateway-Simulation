package com.example.payment.controller;

import com.example.payment.dto.CreateRefundRequest;
import com.example.payment.dto.RefundResponse;
import com.example.payment.entity.Refund;
import com.example.payment.service.RefundService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping
    public ResponseEntity<RefundResponse> createRefund(@Valid @RequestBody CreateRefundRequest request) {

        Refund refund = refundService.createRefund(request);

        RefundResponse response = new RefundResponse();

        response.setRefundReference(refund.getRefundReference());

        response.setPaymentReference(refund.getPayment().getPaymentReference());

        response.setAmount(refund.getAmount());
        response.setStatus(refund.getStatus());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
