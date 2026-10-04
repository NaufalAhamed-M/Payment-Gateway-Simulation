package com.example.payment.dto;

import com.example.payment.entity.Settlement;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class SettlementResponse {

    private final String settlementReference;
    private final Long merchantId;
    private final String paymentReference;
    private final BigDecimal grossAmount;
    private final BigDecimal gatewayFee;
    private final BigDecimal netAmount;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime settledAt;

    public SettlementResponse(Settlement settlement) {
        this.settlementReference = settlement.getSettlementReference();
        this.merchantId = settlement.getMerchant().getId();
        this.paymentReference = settlement.getPayment().getPaymentReference();
        this.grossAmount = settlement.getGrossAmount();
        this.gatewayFee = settlement.getGatewayFee();
        this.netAmount = settlement.getNetAmount();
        this.status = settlement.getStatus().name();
        this.createdAt = settlement.getCreatedAt();
        this.settledAt = settlement.getSettledAt();
    }
}
