package com.farmkart.starter.common.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when a user submits an agent chat request.
 * Consumers (e.g., notification service) can use this for async workflows.
 */
public record AgentTaskCreatedEvent(
    String eventId,
    String eventType,
    String serviceOrigin,
    Instant occurredAt,
    String sessionUuid,
    Long userId,
    String agentRole,
    String userMessage
) implements java.io.Serializable {

    public static AgentTaskCreatedEvent of(String sessionUuid, Long userId, String role, String msg) {
        return new AgentTaskCreatedEvent(
            UUID.randomUUID().toString(),
            "AGENT_TASK_CREATED",
            "farm-kart-agent",
            Instant.now(),
            sessionUuid, userId, role, msg
        );
    }
}
