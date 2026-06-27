package com.farmkart.client.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Verify OTP and complete mobile login")
public record VerifyOtpRequest(
        @Schema(description = "Mobile number OTP was sent to", example = "9876543210", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$") String mobile,
        @Schema(description = "6-digit OTP received via SMS", example = "654321", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(min = 6, max = 6) String otp
) {
}
