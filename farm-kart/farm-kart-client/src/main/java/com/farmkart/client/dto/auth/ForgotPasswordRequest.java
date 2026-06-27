package com.farmkart.client.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request password reset OTP")
public record ForgotPasswordRequest(
        @Schema(description = "Email or mobile of the account", example = "raj@example.com")
        @NotBlank String username) {}
