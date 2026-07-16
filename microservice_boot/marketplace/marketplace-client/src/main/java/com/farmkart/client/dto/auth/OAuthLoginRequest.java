package com.farmkart.client.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Google or Microsoft OAuth login request")
public record OAuthLoginRequest(
        @Schema(description = "OAuth provider", example = "GOOGLE", allowableValues = {"GOOGLE", "MICROSOFT"})
        @NotBlank String provider,
        @Schema(description = "Google ID token (required for GOOGLE provider)")
        String idToken,
        @Schema(description = "Microsoft access token (required for MICROSOFT provider)")
        String accessToken) {}
