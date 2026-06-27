package com.farmkart.client.dto.payment;

import java.util.Map;

public record PaymentWebhookRequest(
        String providerPaymentId,
        Long orderId,
        String status,
        Map<String, Object> metadata
) {
}
