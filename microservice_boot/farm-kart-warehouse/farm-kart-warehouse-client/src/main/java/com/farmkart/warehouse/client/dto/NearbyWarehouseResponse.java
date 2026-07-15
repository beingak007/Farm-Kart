package com.farmkart.warehouse.client.dto;

import java.math.BigDecimal;

public record NearbyWarehouseResponse(
        Long id,
        String name,
        String address,
        String state,
        String district,
        String pincode,
        Double totalCapacityTons,
        Double availableCapacityTons,
        boolean coldStorage,
        String status,
        Double latitude,
        Double longitude,
        BigDecimal pricePerTonPerDay,
        double distanceKm,
        BigDecimal estimatedRent
) {}
