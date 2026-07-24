package com.farmkart.client.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to send OTP to a mobile number")
public record SendOtpRequest(
        @Schema(description = "10-digit Indian mobile number", example = "9876543210", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$") String mobile
) {
}
