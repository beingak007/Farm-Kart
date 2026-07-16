package com.farmkart.buyer.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BuyerOnboardRequest(
        @NotNull Long userId,
        @NotBlank String displayName,
        @NotBlank String address,
        @NotBlank String state,
        @NotBlank String pincode,
        String gstin,
        String companyName,
        String buyerType   // INDIVIDUAL | RETAILER | EXPORTER | WHOLESALER
) {}
