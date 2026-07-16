package com.farmkart.warehouse.client.dto;

import jakarta.validation.constraints.NotNull;

public record PostBookingLocationRequest(
        @NotNull Double latitude,
        @NotNull Double longitude
) {}
