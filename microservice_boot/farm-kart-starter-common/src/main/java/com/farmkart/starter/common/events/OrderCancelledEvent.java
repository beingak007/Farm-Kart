package com.farmkart.starter.common.events;

import java.time.Instant;

public record OrderCancelledEvent(
        FkBaseEvent base,
        Long orderId,
        Long buyerId,
        String reason,
        Instant cancelledAt
) {}
