package com.farmkart.admin.client.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAuditLogRequest(
        Long userId,
        @NotBlank String serviceName,
        @NotBlank String action,
        String resourceType,
        String resourceId,
        String ipAddress,
        String description,
        String outcome
) {}
