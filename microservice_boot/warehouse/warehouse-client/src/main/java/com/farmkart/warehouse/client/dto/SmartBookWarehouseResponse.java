package com.farmkart.warehouse.client.dto;

import java.math.BigDecimal;

public record SmartBookWarehouseResponse(
        Long bookingId,
        Long warehouseId,
        String warehouseName,
        String warehouseAddress,
        double distanceKm,
        BigDecimal totalRent,
        String currency,
        String status
) {}
