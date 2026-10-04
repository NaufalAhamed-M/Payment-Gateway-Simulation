package com.example.payment.controller;

import com.example.payment.dto.CreatePaymentRequest;
import com.example.payment.dto.PaymentCreationResult;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.entity.Payment;
import com.example.payment.service.PaymentProcessingService;
import com.example.payment.service.PaymentResolutionService;
import com.example.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentProcessingService paymentProcessingService;
    private final PaymentResolutionService paymentResolutionService;

    public PaymentController(PaymentService paymentService, PaymentProcessingService paymentProcessingService, PaymentResolutionService paymentResolutionService) {
        this.paymentService = paymentService;
        this.paymentProcessingService = paymentProcessingService;
        this.paymentResolutionService = paymentResolutionService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentCreationResult result = paymentService.createPayment(request);

        Payment payment = result.getPayment();

        PaymentResponse response = new PaymentResponse();

        response.setPaymentReference(payment.getPaymentReference());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setPaymentMethod(payment.getPaymentMethod().name());
        response.setStatus(payment.getStatus());

        if (result.isNewlyCreated()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentReference}/process")
    public ResponseEntity<PaymentResponse> startProcessing(@PathVariable String paymentReference) {

        Payment payment = paymentProcessingService.startProcessingByReference(paymentReference);

        PaymentResponse response = new PaymentResponse();
        response.setPaymentReference(payment.getPaymentReference());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setPaymentMethod(payment.getPaymentMethod().name());
        response.setStatus(payment.getStatus());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentReference}/resolve")
    public ResponseEntity<PaymentResponse> resolvePayment(@PathVariable String paymentReference) {

        Payment payment = paymentResolutionService.resolvePayment(paymentReference);

        PaymentResponse response = new PaymentResponse();
        response.setPaymentReference(payment.getPaymentReference());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setPaymentMethod(payment.getPaymentMethod().name());
        response.setStatus(payment.getStatus());

        return ResponseEntity.ok(response);
    }
}
