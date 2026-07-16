package com.farmkart.warehouse.client.dto;

import java.time.Instant;

public record BookingLiveLocationResponse(
        Long bookingId,
        Long warehouseId,
        Double latitude,
        Double longitude,
        String mapsUrl,
        Instant updatedAt
) {}
