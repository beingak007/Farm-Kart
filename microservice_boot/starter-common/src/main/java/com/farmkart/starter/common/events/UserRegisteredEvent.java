package com.farmkart.starter.common.events;

import java.time.Instant;

public record UserRegisteredEvent(
        FkBaseEvent base,
        Long userId,
        String email,
        String mobile,
        String role,
        Instant registeredAt
) {}
