package com.farmkart.admin.client.dto;

import java.time.Instant;

public record AuditLogResponse(
        Long id, Long userId, String serviceName, String action,
        String resourceType, String resourceId, String ipAddress,
        String description, String outcome, Instant createdAt
) {}
