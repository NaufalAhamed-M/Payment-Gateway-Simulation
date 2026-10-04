package com.example.payment.entity;

public enum PaymentAttemptStatus {
    CREATED,
    PROCESSING,
    SUCCESS,
    FAILED,
    NOT_FOUND, TIMEOUT
}
