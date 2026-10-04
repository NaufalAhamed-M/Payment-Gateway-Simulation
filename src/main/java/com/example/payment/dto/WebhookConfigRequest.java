package com.example.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookConfigRequest {
    @NotBlank
    private String webhookUrl;
}
