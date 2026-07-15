package com.farmkart.warehouse.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Uber-style warehouse booking: match nearest available warehouse within radius and confirm.
 */
public record SmartBookWarehouseRequest(
        @NotNull Long farmerId,
        @NotBlank String farmerContact,
        @NotNull String cropName,
        @NotNull Double quantityTons,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull Double pickupLatitude,
        @NotNull Double pickupLongitude,
        Double radiusKm,
        String currency
) {}
