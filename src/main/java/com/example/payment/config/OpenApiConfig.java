package com.example.payment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Payment Gateway Simulator API",
                version = "1.0",
                description = """
                        REST API for a simulated payment gateway.

                        Supports merchant management, payment processing,
                        idempotency, retries, refunds, webhooks,
                        settlement, reconciliation and audit logging.
                        """
        )
)
public class OpenApiConfig {
}
