package com.example.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WebhookConfigResponse {
    private Long merchantId;
    private String webhookUrl;
    private String webhookSecret;
}
