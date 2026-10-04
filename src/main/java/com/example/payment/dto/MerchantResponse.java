package com.example.payment.dto;

import com.example.payment.entity.MerchantStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MerchantResponse {

    private Long id;
    private String name;
    private String email;
    private MerchantStatus status;
    private LocalDateTime createdAt;
}
