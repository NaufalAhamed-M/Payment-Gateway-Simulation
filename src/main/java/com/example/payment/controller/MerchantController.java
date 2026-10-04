package com.example.payment.controller;

import com.example.payment.dto.CreateMerchantRequest;
import com.example.payment.dto.MerchantResponse;
import com.example.payment.dto.WebhookConfigRequest;
import com.example.payment.dto.WebhookConfigResponse;
import com.example.payment.entity.Merchant;
import com.example.payment.service.MerchantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MerchantResponse createMerchant(@Validated @RequestBody CreateMerchantRequest request) {
        return merchantService.createMerchant(request);
    }

    @PutMapping("/{merchantId}/webhook")
    public WebhookConfigResponse configureWebhook(@PathVariable Long merchantId, @Valid @RequestBody WebhookConfigRequest request) {
        return merchantService.configureWebhook(merchantId, request);
    }
}
