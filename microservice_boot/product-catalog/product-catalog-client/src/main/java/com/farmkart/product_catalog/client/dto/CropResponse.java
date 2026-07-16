package com.farmkart.product_catalog.client.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record CropResponse(
        Long id,
        String name,
        String slug,
        Long categoryId,
        String categoryName,
        String description,
        String imageUrl,
        String unit,
        BigDecimal basePrice,
        String harvestSeason,
        String gradeStandard,
        Boolean isOrganic,
        Boolean isActive,
        Instant createdAt
) {}
