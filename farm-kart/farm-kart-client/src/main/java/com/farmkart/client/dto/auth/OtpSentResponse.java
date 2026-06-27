package com.farmkart.client.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Confirmation that OTP was sent via SMS")
public record OtpSentResponse(
        @Schema(description = "Whether OTP dispatch succeeded", example = "true")
        boolean otpSent
) {
}
