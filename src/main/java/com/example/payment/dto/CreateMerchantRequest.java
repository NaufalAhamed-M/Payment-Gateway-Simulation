package com.example.payment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMerchantRequest {

    @NotBlank(message = "Merchant name is required")
    private String name;

    @NotBlank(message = "Merchant email is required")
    @Email(message = "Merchant email must be valid")
    private String email;
}
