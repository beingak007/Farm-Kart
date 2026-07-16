package com.farmkart.starter.common.events;

import java.time.Instant;

public record BuyerCreatedEvent(
        FkBaseEvent base,
        Long buyerId,
        Long userId,
        String displayName,
        String state,
        Instant registeredAt
) {}
