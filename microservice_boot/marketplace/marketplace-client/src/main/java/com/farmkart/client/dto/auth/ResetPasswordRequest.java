package com.farmkart.client.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Reset password using OTP received on mobile")
public record ResetPasswordRequest(
        @Schema(description = "Email or mobile of the account", example = "raj@example.com")
        @NotBlank String username,
        @Schema(description = "6-digit reset OTP", example = "654321")
        @NotBlank String otp,
        @Schema(description = "New password (min 6 characters)", example = "NewSecurePass123")
        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String newPassword) {}
