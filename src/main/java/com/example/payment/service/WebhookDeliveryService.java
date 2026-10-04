package com.example.payment.service;

import com.example.payment.entity.WebhookEvent;
import com.example.payment.entity.WebhookEventStatus;
import com.example.payment.repository.WebhookEventRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class WebhookDeliveryService {

    private final WebhookEventRepository webhookEventRepository;
    private final RestClient restClient;
    private static final int MAX_RETRIES = 3;

    public WebhookDeliveryService(WebhookEventRepository webhookEventRepository, RestClient.Builder restClientBuilder) {
        this.webhookEventRepository = webhookEventRepository;
        this.restClient = restClientBuilder.build();
    }

    @Transactional
    public void deliverWebhook(String eventReference) {

        WebhookEvent event = webhookEventRepository
                .findByEventReference(eventReference)
                .orElseThrow(() ->
                        new RuntimeException("Webhook event not found"));

        if (event.getStatus() == WebhookEventStatus.DELIVERED) {
            return;
        }

        if (event.getRetryCount() >= MAX_RETRIES) {
            event.setStatus(WebhookEventStatus.FAILED);
            webhookEventRepository.save(event);
            return;
        }

        event.setStatus(WebhookEventStatus.PROCESSING);
        event.setRetryCount(event.getRetryCount() + 1);
        webhookEventRepository.save(event);

        try {
            String signature = HmacUtil.generateHmac(
                    event.getPayload(),
                    event.getMerchant().getWebhookSecret()
            );

            ResponseEntity<String> response = restClient
                    .post()
                    .uri(event.getMerchant().getWebhookUrl())
                    .header("Content-Type", "application/json")
                    .header("X-Webhook-Signature", signature)
                    .body(event.getPayload())
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                event.setStatus(WebhookEventStatus.DELIVERED);
                event.setDeliveredAt(LocalDateTime.now());
            } else {
                event.setStatus(WebhookEventStatus.FAILED);
            }
        } catch (RestClientException exception) {
            event.setStatus(WebhookEventStatus.FAILED);
        }
        webhookEventRepository.save(event);
    }
}
