package com.farmkart.product_catalog.client.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank String name,
        @NotBlank String slug,
        String description,
        Long parentCategoryId,
        String imageUrl
) {}
