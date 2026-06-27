package com.farmkart.logistics.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateShipmentRequest(
        @NotNull Long orderId,
        Long logisticsPartnerId,
        @NotBlank String pickupAddress,
        @NotBlank String deliveryAddress,
        Double weightKg,
        Instant expectedDelivery,
        String notes
) {}
