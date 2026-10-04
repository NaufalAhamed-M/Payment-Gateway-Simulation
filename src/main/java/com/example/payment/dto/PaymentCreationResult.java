package com.example.payment.dto;

import com.example.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentCreationResult {
    private Payment payment;
    private boolean NewlyCreated;
}