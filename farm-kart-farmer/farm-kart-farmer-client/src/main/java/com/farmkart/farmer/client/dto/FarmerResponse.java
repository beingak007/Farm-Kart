package com.farmkart.farmer.client.dto;

import com.farmkart.farmer.client.enums.FarmerStatus;

import java.time.Instant;

public record FarmerResponse(
        Long id,
        Long userId,
        String farmName,
        String address,
        String state,
        String district,
        String pincode,
        Double farmAreaAcres,
        String primaryCrop,
        FarmerStatus status,
        Instant createdAt
) {}
