package com.farmkart.client.dto.catalog;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateProductRequest(
        @NotNull Long vendorId,
        @NotNull Long categoryId,
        @NotBlank String title,
        String description,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotBlank String unit,
        @NotNull @DecimalMin("0") BigDecimal stockQuantity,
        @NotNull @DecimalMin("0.001") BigDecimal minOrderQuantity,
        List<String> images,
        boolean organic,
        LocalDate harvestDate,
        String storageInstructions
) {
}
