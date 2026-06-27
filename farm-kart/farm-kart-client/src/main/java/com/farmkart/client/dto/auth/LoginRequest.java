package com.farmkart.client.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Username/password login request")
public record LoginRequest(
        @Schema(description = "Email or mobile number", example = "raj@example.com")
        @NotBlank String username,
        @Schema(description = "Account password", example = "SecurePass123")
        @NotBlank String password
) {}
