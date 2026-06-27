package com.farmkart.client.dto.catalog;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProductResponse(
        Long id,
        Long vendorId,
        Long categoryId,
        String title,
        String description,
        BigDecimal price,
        String unit,
        BigDecimal stockQuantity,
        BigDecimal minOrderQuantity,
        List<String> images,
        boolean organic,
        LocalDate harvestDate,
        String storageInstructions
) {
}
