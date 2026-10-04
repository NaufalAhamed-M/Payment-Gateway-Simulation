package com.example.payment.service;

import com.example.payment.dto.CreateMerchantRequest;
import com.example.payment.dto.MerchantResponse;
import com.example.payment.dto.WebhookConfigResponse;
import com.example.payment.dto.WebhookConfigRequest;
import com.example.payment.entity.Merchant;
import com.example.payment.entity.MerchantStatus;
import com.example.payment.repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public MerchantResponse createMerchant(CreateMerchantRequest request) {

        Merchant merchant = new Merchant(
                request.getName(),
                request.getEmail(),
                MerchantStatus.ACTIVE,
                LocalDateTime.now()
        );

        Merchant savedMerchant = merchantRepository.save(merchant);

        return new MerchantResponse(
                savedMerchant.getId(),
                savedMerchant.getName(),
                savedMerchant.getEmail(),
                savedMerchant.getStatus(),
                savedMerchant.getCreatedAt()
        );
    }
    public WebhookConfigResponse configureWebhook(Long merchantId, WebhookConfigRequest request) {

        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("Merchant not found"));

        String secret = UUID.randomUUID().toString();

        merchant.setWebhookUrl(request.getWebhookUrl());
        merchant.setWebhookSecret(secret);

        merchantRepository.save(merchant);

        return new WebhookConfigResponse(
                merchant.getId(),
                merchant.getWebhookUrl(),
                merchant.getWebhookSecret()
        );
    }
}
