package com.farmkart.ai_advisory.client.dto;

import jakarta.validation.constraints.NotBlank;

public record DemandForecastRequest(
        @NotBlank String cropName,
        String state,
        int forecastWeeks   // 1–52
) {}
