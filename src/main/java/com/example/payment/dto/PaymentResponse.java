package com.example.payment.dto;

import com.example.payment.entity.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResponse {
    private String paymentReference;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private PaymentStatus status;
}
