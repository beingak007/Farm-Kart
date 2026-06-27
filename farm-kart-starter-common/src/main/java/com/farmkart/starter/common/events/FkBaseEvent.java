package com.farmkart.starter.common.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Base event envelope for all Farm Kart Kafka messages.
 * Every domain event extends or embeds this structure.
 */
public record FkBaseEvent(
        String eventId,       // UUID v4 — idempotency key
        String eventType,     // e.g. "farmkart.order.created"
        String serviceOrigin, // e.g. "order-service"
        Instant occurredAt
) {
    public FkBaseEvent(String eventType, String serviceOrigin) {
        this(UUID.randomUUID().toString(), eventType, serviceOrigin, Instant.now());
    }
}
