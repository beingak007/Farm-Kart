package com.farmkart.client.dto.payment;

import com.farmkart.client.enums.PaymentRecordStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        Long id,
        Long orderId,
        String paymentProvider,
        String providerPaymentId,
        BigDecimal amount,
        PaymentRecordStatus status
) {
}
