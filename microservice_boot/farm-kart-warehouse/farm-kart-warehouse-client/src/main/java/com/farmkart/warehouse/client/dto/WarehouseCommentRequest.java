package com.farmkart.warehouse.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WarehouseCommentRequest(
        @NotNull Long farmerId,
        @NotBlank String body,
        Long ratingId
) {}
