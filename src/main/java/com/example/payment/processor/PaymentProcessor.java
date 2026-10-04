package com.example.payment.processor;

import com.example.payment.entity.PaymentAttempt;

public interface PaymentProcessor {
    PaymentProcessorResult process(PaymentAttempt attempt);
    PaymentProcessorResult checkStatus(PaymentAttempt attempt);
}
