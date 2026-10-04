package com.example.payment.service;

import com.example.payment.entity.Merchant;
import com.example.payment.entity.Payment;
import com.example.payment.entity.WebhookEvent;
import com.example.payment.repository.WebhookEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final WebhookEventRepository webhookEventRepository;
    private final ObjectMapper objectMapper;

    public WebhookService(WebhookEventRepository webhookEventRepository, ObjectMapper objectMapper) {
        this.webhookEventRepository = webhookEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public WebhookEvent createPaymentWebhook(Payment payment) {

        Merchant merchant = payment.getMerchant();

        String eventType = "payment." + payment.getStatus().name().toLowerCase();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventType", eventType);
        payload.put("paymentReference", payment.getPaymentReference());
        payload.put("amount", payment.getAmount());
        payload.put("currency", payment.getCurrency());
        payload.put("paymentMethod", payment.getPaymentMethod());
        payload.put("status", payment.getStatus());

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            WebhookEvent event = new WebhookEvent(
                    merchant,
                    payment,
                    eventType,
                    jsonPayload
            );

            return webhookEventRepository.save(event);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create webhook payload", e);
        }
    }
}
