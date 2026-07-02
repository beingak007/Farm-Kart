package com.farmkart.warehouse.client.dto;

import java.time.Instant;

public record WarehouseCommentResponse(
        Long id,
        Long farmerId,
        Long ratingId,
        String body,
        Instant createdAt
) {}
