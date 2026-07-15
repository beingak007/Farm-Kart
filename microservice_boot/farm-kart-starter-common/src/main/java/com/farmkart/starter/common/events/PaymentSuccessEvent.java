package com.farmkart.starter.common.events;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentSuccessEvent(
        FkBaseEvent base,
        Long paymentId,
        Long orderId,
        Long userId,
        BigDecimal amount,
        String currency,
        String gatewayTransactionId,
        Instant paidAt
) {}
