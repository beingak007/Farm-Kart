package com.farmkart.starter.common.events;

import java.time.Instant;

public record WarehouseLiveLocationEvent(
        FkBaseEvent base,
        Long bookingId,
        Long warehouseId,
        String warehouseName,
        Long farmerId,
        String farmerContact,
        Double latitude,
        Double longitude,
        String mapsUrl,
        Instant updatedAt
) {}
