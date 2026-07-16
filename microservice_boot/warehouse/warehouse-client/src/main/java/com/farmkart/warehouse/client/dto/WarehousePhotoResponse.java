package com.farmkart.warehouse.client.dto;

import java.time.Instant;

public record WarehousePhotoResponse(
        Long id,
        String url,
        String caption,
        int sortOrder,
        Instant createdAt
) {}
