package com.farmkart.client.dto.vendor;

import com.farmkart.client.enums.VendorStatus;

import java.time.Instant;

public record VendorResponse(
        Long id,
        Long userId,
        String farmName,
        String address,
        String state,
        String district,
        String pincode,
        VendorStatus status,
        Instant createdAt
) {
}
