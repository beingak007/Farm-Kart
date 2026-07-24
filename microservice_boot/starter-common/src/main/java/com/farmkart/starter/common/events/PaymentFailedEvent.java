package com.farmkart.starter.common.events;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentFailedEvent(
        FkBaseEvent base,
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        String currency,
        String failureReason,
        Instant failedAt
) {}
