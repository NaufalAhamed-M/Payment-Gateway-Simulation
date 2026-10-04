package com.example.payment.dto;

import com.example.payment.entity.RefundStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RefundResponse {
    private String refundReference;
    private String paymentReference;
    private BigDecimal amount;
    private RefundStatus status;
}
