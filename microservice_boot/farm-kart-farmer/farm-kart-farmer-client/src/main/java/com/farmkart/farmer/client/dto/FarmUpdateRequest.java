package com.farmkart.farmer.client.dto;

public record FarmUpdateRequest(
        String farmName,
        String address,
        String state,
        String district,
        String pincode,
        Double farmAreaAcres,
        String primaryCrop
) {}
