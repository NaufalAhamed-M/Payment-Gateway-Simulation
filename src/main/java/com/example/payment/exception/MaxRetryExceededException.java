package com.example.payment.exception;

public class MaxRetryExceededException extends RuntimeException {

    public MaxRetryExceededException(String message) {
        super(message);
    }
}
