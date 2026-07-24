package com.farmkart.client.dto.auth;

import com.farmkart.client.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "New user registration request")
public record RegisterRequest(
        @Schema(description = "Full name", example = "Raj Kumar")
        @NotBlank @Size(max = 255) String name,
        @Schema(description = "Email address", example = "raj@example.com")
        @NotBlank @Email String email,
        @Schema(description = "10-digit mobile number", example = "9876543210")
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$") String mobile,
        @Schema(description = "User role (defaults to VENDOR)", example = "VENDOR")
        UserRole role,
        @Schema(description = "Password (min 8 characters, optional for OTP-only accounts)", example = "SecurePass123")
        @Size(min = 8, max = 100) String password
) {
}
