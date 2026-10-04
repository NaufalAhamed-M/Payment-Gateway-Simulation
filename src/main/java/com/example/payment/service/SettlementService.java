package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.payment.entity.Settlement;
import com.example.payment.entity.SettlementStatus;
import com.example.payment.repository.SettlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class SettlementService {

    private static final BigDecimal FEE_RATE = new BigDecimal("0.02");

    private final SettlementRepository settlementRepository;

    public SettlementService(SettlementRepository settlementRepository) {
        this.settlementRepository = settlementRepository;
    }

    @Transactional
    public Settlement createSettlement(Payment payment) {

        if (payment.getStatus() != com.example.payment.entity.PaymentStatus.SUCCESS) {
            throw new RuntimeException("Only successful payments can be settled");
        }

        if (settlementRepository.findByPaymentId(payment.getId()).isPresent()) {
            throw new RuntimeException("Settlement already exists for this payment");
        }

        BigDecimal grossAmount = payment.getAmount();

        BigDecimal gatewayFee = grossAmount
                .multiply(FEE_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal netAmount = grossAmount
                .subtract(gatewayFee)
                .setScale(2, RoundingMode.HALF_UP);

        Settlement settlement = new Settlement(
                payment.getMerchant(),
                payment,
                grossAmount,
                gatewayFee,
                netAmount
        );

        return settlementRepository.save(settlement);
    }
    @Transactional
    public Settlement processSettlement(String settlementReference) {

        Settlement settlement = settlementRepository
                .findBySettlementReference(settlementReference)
                .orElseThrow(() ->
                        new RuntimeException("Settlement not found"));

        if (settlement.getStatus() != SettlementStatus.PENDING) {
            throw new RuntimeException(
                    "Settlement cannot be processed from status: "
                            + settlement.getStatus()
            );
        }

        settlement.setStatus(SettlementStatus.PROCESSING);
        settlementRepository.save(settlement);

        settlement.setStatus(SettlementStatus.SETTLED);
        settlement.setSettledAt(LocalDateTime.now());

        return settlementRepository.save(settlement);
    }
}
