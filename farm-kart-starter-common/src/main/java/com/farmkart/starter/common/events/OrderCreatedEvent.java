package com.farmkart.starter.common.events;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreatedEvent(
        FkBaseEvent base,
        Long orderId,
        Long buyerId,
        Long vendorId,
        BigDecimal totalAmount,
        String currency,
        Instant orderedAt
) {}
