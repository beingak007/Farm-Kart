package com.farmkart.starter.common.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record WarehouseBookedEvent(
        FkBaseEvent base,
        Long bookingId,
        Long warehouseId,
        String warehouseName,
        String warehouseAddress,
        Long farmerId,
        String farmerContact,
        String cropName,
        Double quantityTons,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalRent,
        String currency,
        Double distanceKm,
        Instant bookedAt
) {}
