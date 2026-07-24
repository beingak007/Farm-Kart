package com.farmkart.client.dto.auth;

import com.farmkart.client.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication tokens returned after successful login")
public record AuthTokenResponse(
        @Schema(description = "JWT access token (use as Bearer token)", example = "eyJhbGciOiJIUzUxMiJ9...")
        String accessToken,
        @Schema(description = "Refresh token for obtaining new access tokens")
        String refreshToken,
        @Schema(description = "Authenticated user ID", example = "101")
        Long userId,
        @Schema(description = "User role", example = "VENDOR")
        UserRole role,
        @Schema(description = "True when OTP was sent but login not yet complete (registration flow)")
        boolean otpSent
) {
}
