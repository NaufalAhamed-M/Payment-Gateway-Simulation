package com.example.payment.processor;

import com.example.payment.entity.PaymentAttempt;
import com.example.payment.entity.PaymentAttemptStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SimulatedPaymentProcessor implements PaymentProcessor {

    private PaymentAttemptStatus simulatedStatus = PaymentAttemptStatus.SUCCESS;
    private PaymentAttemptStatus statusCheckResult = PaymentAttemptStatus.NOT_FOUND;

    public void setStatusCheckResult(PaymentAttemptStatus statusCheckResult) {
        this.statusCheckResult = statusCheckResult;
    }

    @Override
    public PaymentProcessorResult process(PaymentAttempt attempt) {
        if (simulatedStatus == PaymentAttemptStatus.SUCCESS) {
            return new PaymentProcessorResult(
                    PaymentAttemptStatus.SUCCESS,
                    "PROC-" + UUID.randomUUID(),
                    null
            );
        }
        if (simulatedStatus == PaymentAttemptStatus.FAILED) {
            return new PaymentProcessorResult(
                    PaymentAttemptStatus.FAILED,
                    "PROC-" + UUID.randomUUID(),
                    "Payment declined by simulated processor"
            );
        }
        if (simulatedStatus == PaymentAttemptStatus.TIMEOUT) {
            return new PaymentProcessorResult(
                    PaymentAttemptStatus.TIMEOUT,
                    null,
                    "Processor response timed out"
            );
        }
        throw new IllegalStateException("Unsupported simulated processor status");
    }

    @Override
    public PaymentProcessorResult checkStatus(PaymentAttempt attempt) {

        if (statusCheckResult == PaymentAttemptStatus.NOT_FOUND) {

            return new PaymentProcessorResult(
                    PaymentAttemptStatus.NOT_FOUND,
                    null,
                    "Original transaction not found"
            );
        }

        return new PaymentProcessorResult(PaymentAttemptStatus.SUCCESS, attempt.getProcessorReference(),null);
    }
}
