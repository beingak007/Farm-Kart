package com.farmkart.ai_advisory.client.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record DemandForecastResponse(
        String cropName,
        String state,
        List<WeeklyForecast> weeklyForecasts,
        String trend,       // RISING | STABLE | FALLING
        String confidence,  // HIGH | MEDIUM | LOW
        Instant generatedAt
) {
    public record WeeklyForecast(
            int weekNumber,
            BigDecimal forecastedPricePerQuintal,
            BigDecimal estimatedDemandTons,
            String notes
    ) {}
}
