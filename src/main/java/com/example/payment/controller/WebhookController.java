package com.example.payment.controller;

import com.example.payment.service.WebhookDeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final WebhookDeliveryService webhookDeliveryService;

    public WebhookController(WebhookDeliveryService webhookDeliveryService) {
        this.webhookDeliveryService = webhookDeliveryService;
    }

    @PostMapping("/{eventReference}/deliver")
    public ResponseEntity<String> deliverWebhook(@PathVariable String eventReference) {

        webhookDeliveryService.deliverWebhook(eventReference);

        return ResponseEntity.ok("Webhook delivery attempted");
    }
}
