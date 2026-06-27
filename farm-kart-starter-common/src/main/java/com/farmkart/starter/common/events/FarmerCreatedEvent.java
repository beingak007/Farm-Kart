package com.farmkart.starter.common.events;

import java.time.Instant;

public record FarmerCreatedEvent(
        FkBaseEvent base,
        Long farmerId,
        Long userId,
        String farmName,
        String state,
        String district,
        Instant registeredAt
) {}
