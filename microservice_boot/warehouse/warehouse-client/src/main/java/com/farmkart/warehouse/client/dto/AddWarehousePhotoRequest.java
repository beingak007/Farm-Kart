package com.farmkart.warehouse.client.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddWarehousePhotoRequest(
        @NotBlank String s3Key,
        String caption,
        int sortOrder
) {}
