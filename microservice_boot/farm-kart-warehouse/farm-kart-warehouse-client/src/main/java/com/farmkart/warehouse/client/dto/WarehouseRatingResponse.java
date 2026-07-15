package com.farmkart.warehouse.client.dto;

import java.time.Instant;

public record WarehouseRatingResponse(
        Long id,
        Long farmerId,
        int stars,
        Instant createdAt
) {}
