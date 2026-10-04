package com.example.payment.service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public final class HmacUtil {

    private HmacUtil() {
    }

    public static String generateHmac(String payload, String secret) {

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
            );

            mac.init(secretKey);

            byte[] hash = mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            return result.toString();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate webhook signature", e);
        }
    }
}
