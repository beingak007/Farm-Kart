package com.farmkart.ai_advisory.client.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CropRecommendationResponse(
        Long id,
        Long farmerId,
        String season,
        List<RecommendedCrop> recommendations,
        String advisoryNotes,
        Instant generatedAt
) {
    public record RecommendedCrop(
            String cropName,
            BigDecimal expectedYieldTonsPerAcre,
            BigDecimal estimatedRevenuePer100Kg,
            int demandScore,           // 1-100
            String riskLevel,          // LOW | MEDIUM | HIGH
            String rationale
    ) {}
}
