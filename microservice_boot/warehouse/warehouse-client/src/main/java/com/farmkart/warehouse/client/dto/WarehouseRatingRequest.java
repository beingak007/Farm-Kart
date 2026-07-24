package com.farmkart.warehouse.client.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WarehouseRatingRequest(
        @NotNull Long farmerId,
        @NotNull @Min(1) @Max(5) Integer stars
) {}
