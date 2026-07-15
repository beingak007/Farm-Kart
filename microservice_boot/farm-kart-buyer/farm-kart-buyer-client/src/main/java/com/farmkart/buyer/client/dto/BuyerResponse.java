package com.farmkart.buyer.client.dto;

import java.time.Instant;

public record BuyerResponse(
        Long id,
        Long userId,
        String displayName,
        String address,
        String state,
        String pincode,
        String gstin,
        String companyName,
        String buyerType,
        String status,
        Instant createdAt
) {}
