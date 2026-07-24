package com.farmkart.warehouse.client.dto;

public record WarehouseResponse(
        Long id, String name, String address, String state, String district, String pincode,
        Double totalCapacityTons, Double availableCapacityTons, boolean coldStorage, String status
) {}
