package com.farmkart.product_catalog.client.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateCropRequest(
        @NotBlank String name,
        @NotBlank String slug,
        @NotNull Long categoryId,
        String description,
        String imageUrl,
        @NotBlank String unit,                   // KG | QUINTAL | TON
        @NotNull @DecimalMin("0.01") BigDecimal basePrice,
        String harvestSeason,
        String gradeStandard,                    // A | B | C | ORGANIC
        Boolean isOrganic
) {}
