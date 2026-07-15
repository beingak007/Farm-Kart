package com.farmkart.ai_advisory.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CropRecommendationRequest(
        @NotNull Long farmerId,
        @NotBlank String state,
        @NotBlank String district,
        @NotBlank String season,       // RABI | KHARIF | ZAID
        Double farmAreaAcres,
        String soilType,               // CLAY | SANDY | LOAMY | BLACK | RED
        String currentCrop,
        String irrigationAvailable     // YES | NO | PARTIAL
) {}
