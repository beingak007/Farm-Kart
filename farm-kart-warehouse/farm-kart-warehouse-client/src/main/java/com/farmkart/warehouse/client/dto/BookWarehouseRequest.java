package com.farmkart.warehouse.client.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookWarehouseRequest(
        @NotNull Long warehouseId,
        @NotNull Long farmerId,
        @NotNull String cropName,
        @NotNull Double quantityTons,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {}
