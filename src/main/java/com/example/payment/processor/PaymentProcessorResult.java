package com.example.payment.processor;

import com.example.payment.entity.PaymentAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessorResult {
    private PaymentAttemptStatus status;
    private String processorReference;
    private String failureReason;
}