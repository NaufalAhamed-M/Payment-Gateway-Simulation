package com.example.payment.service;

import com.example.payment.dto.CreatePaymentRequest;
import com.example.payment.dto.PaymentCreationResult;
import com.example.payment.entity.Merchant;
import com.example.payment.entity.MerchantStatus;
import com.example.payment.entity.Payment;
import com.example.payment.entity.PaymentMethod;
import com.example.payment.entity.PaymentStatus;
import com.example.payment.repository.MerchantRepository;
import com.example.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Merchant merchant;
    private CreatePaymentRequest request;

    @BeforeEach
    void setUp() {

        merchant = new Merchant(
                "Test Merchant",
                "test@example.com",
                MerchantStatus.ACTIVE,
                LocalDateTime.now()
        );

        request = new CreatePaymentRequest();
        request.setMerchantId(1L);
        request.setAmount(new BigDecimal("3000.00"));
        request.setCurrency("INR");
        request.setPaymentMethod("UPI");
        request.setIdempotencyKey("test-key-001");
    }

    @Test
    void shouldCreateNewPayment() {

        when(merchantRepository.findById(1L)).thenReturn(Optional.of(merchant));

        when(paymentRepository
                .findByMerchantIdAndIdempotencyKey(
                        1L,
                        "test-key-001"
                ))
                .thenReturn(Optional.empty());

        Payment savedPayment = new Payment(
                "payment-ref-001",
                merchant,
                request.getAmount(),
                request.getCurrency(),
                PaymentMethod.UPI,
                PaymentStatus.CREATED,
                request.getIdempotencyKey(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);

        PaymentCreationResult result =
                paymentService.createPayment(request);

        assertNotNull(result);
        assertTrue(result.isNewlyCreated());

        Payment payment = result.getPayment();

        assertNotNull(payment);
        assertEquals(
                "payment-ref-001",
                payment.getPaymentReference()
        );

        assertEquals(
                PaymentStatus.CREATED,
                payment.getStatus()
        );

        assertEquals(
                new BigDecimal("3000.00"),
                payment.getAmount()
        );

        verify(paymentRepository)
                .save(any(Payment.class));
    }

    @Test
    void shouldReturnExistingPaymentForSameIdempotencyKey() {

        when(merchantRepository.findById(1L))
                .thenReturn(Optional.of(merchant));

        Payment existingPayment = new Payment(
                "payment-ref-existing",
                merchant,
                new BigDecimal("3000.00"),
                "INR",
                PaymentMethod.UPI,
                PaymentStatus.CREATED,
                "test-key-001",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(paymentRepository
                .findByMerchantIdAndIdempotencyKey(
                        1L,
                        "test-key-001"
                ))
                .thenReturn(Optional.of(existingPayment));

        PaymentCreationResult result =
                paymentService.createPayment(request);

        assertNotNull(result);

        assertFalse(result.isNewlyCreated());

        assertEquals(
                "payment-ref-existing",
                result.getPayment().getPaymentReference()
        );

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }
}
