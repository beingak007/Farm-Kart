package com.farmkart.logistics.client.dto;

import java.time.Instant;

public record ShipmentResponse(
        Long id,
        Long orderId,
        Long logisticsPartnerId,
        String trackingNumber,
        String pickupAddress,
        String deliveryAddress,
        String status,
        Instant expectedDelivery,
        Instant actualDelivery,
        Double weightKg,
        String notes,
        Instant createdAt
) {}
