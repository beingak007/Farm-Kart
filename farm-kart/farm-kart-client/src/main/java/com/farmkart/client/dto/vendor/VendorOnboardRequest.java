package com.farmkart.client.dto.vendor;

import com.farmkart.client.enums.VendorStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VendorOnboardRequest(
        @NotNull Long userId,
        @NotBlank String farmName,
        String address,
        String state,
        String district,
        String pincode,
        String bankAccount,
        String ifsc,
        String kycDocumentUrl
) {
}
