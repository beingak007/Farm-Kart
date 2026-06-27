package com.farmkart.farmer.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FarmerOnboardRequest(
        @NotNull Long userId,
        @NotBlank @Size(max = 200) String farmName,
        @NotBlank String address,
        @NotBlank String state,
        @NotBlank String district,
        @NotBlank @Size(min = 6, max = 6) String pincode,
        Double farmAreaAcres,
        String primaryCrop,
        String bankAccount,
        String ifsc,
        String aadhaarNumber,
        String panNumber
) {}
