package com.farmkart.ai_advisory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmkart.ai_advisory.client.dto.CropRecommendationRequest;
import com.farmkart.ai_advisory.client.dto.CropRecommendationResponse;
import com.farmkart.ai_advisory.client.dto.CropRecommendationResponse.RecommendedCrop;
import com.farmkart.ai_advisory.client.dto.DemandForecastRequest;
import com.farmkart.ai_advisory.client.dto.DemandForecastResponse;
import com.farmkart.ai_advisory.client.dto.DemandForecastResponse.WeeklyForecast;
import com.farmkart.ai_advisory.repository.AdvisoryLogRepository;
import com.farmkart.ai_advisory.repository.entity.AdvisoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * AI Advisory Service — rule-based crop recommendations and demand forecasting.
 *
 * <p>In production, the {@code recommendCrops} and {@code forecastDemand} methods
 * will call an external ML model endpoint (Python/FastAPI) or a cloud AI service
 * (AWS SageMaker / Azure ML) and return their predictions.
 * The current implementation uses deterministic rule-based logic as a placeholder.
 */
@Service
public class AiAdvisoryService {

    private final AdvisoryLogRepository advisoryRepo;
    private final ObjectMapper objectMapper;

    public AiAdvisoryService(AdvisoryLogRepository advisoryRepo, ObjectMapper objectMapper) {
        this.advisoryRepo = advisoryRepo;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CropRecommendationResponse recommendCrops(CropRecommendationRequest req) {
        List<RecommendedCrop> recs = buildRecommendations(req);
        String advisory = buildAdvisoryNotes(req);

        CropRecommendationResponse response = new CropRecommendationResponse(
                null, req.farmerId(), req.season(), recs, advisory, Instant.now());

        persistLog(req.farmerId(), "CROP_RECOMMENDATION", req, response);
        return response;
    }

    @Transactional
    public DemandForecastResponse forecastDemand(DemandForecastRequest req) {
        List<WeeklyForecast> forecasts = buildWeeklyForecasts(req);
        String trend = determineTrend(req.cropName());
        String confidence = "MEDIUM";

        DemandForecastResponse response = new DemandForecastResponse(
                req.cropName(), req.state(), forecasts, trend, confidence, Instant.now());

        persistLog(null, "DEMAND_FORECAST", req, response);
        return response;
    }

    @Transactional(readOnly = true)
    public Page<AdvisoryLog> getHistory(Long farmerId, Pageable pageable) {
        return advisoryRepo.findByFarmerId(farmerId, pageable);
    }

    // ── Rule-based logic (replace with ML model calls in production) ──────

    private List<RecommendedCrop> buildRecommendations(CropRecommendationRequest req) {
        List<RecommendedCrop> recs = new ArrayList<>();
        String season = req.season().toUpperCase();

        if ("RABI".equals(season)) {
            recs.add(new RecommendedCrop("Wheat", new BigDecimal("1.8"), new BigDecimal("2200"), 85, "LOW",
                    "High mandi demand in " + req.state() + "; good rabi crop for irrigated land."));
            recs.add(new RecommendedCrop("Mustard", new BigDecimal("0.9"), new BigDecimal("5200"), 78, "LOW",
                    "Oilseed with stable MSP support; low water requirement."));
            recs.add(new RecommendedCrop("Chickpea", new BigDecimal("0.7"), new BigDecimal("5100"), 72, "MEDIUM",
                    "Good pulse demand; suitable for rainfed areas."));
        } else if ("KHARIF".equals(season)) {
            recs.add(new RecommendedCrop("Soybean", new BigDecimal("1.2"), new BigDecimal("4200"), 80, "MEDIUM",
                    "Export demand rising; suits black cotton soil."));
            recs.add(new RecommendedCrop("Maize", new BigDecimal("2.1"), new BigDecimal("1900"), 88, "LOW",
                    "Poultry feed demand driving strong prices."));
            recs.add(new RecommendedCrop("Groundnut", new BigDecimal("0.8"), new BigDecimal("4800"), 75, "MEDIUM",
                    "Oilseed with good local demand."));
        } else {
            recs.add(new RecommendedCrop("Vegetables (Mixed)", new BigDecimal("3.0"), new BigDecimal("2800"), 90, "MEDIUM",
                    "Short duration; high returns in summer season."));
        }
        return recs;
    }

    private String buildAdvisoryNotes(CropRecommendationRequest req) {
        return String.format("For %s season in %s, %s: Soil moisture management is critical. " +
                        "Ensure MSP awareness before selling. Consider direct buyer contracts on Farm Kart.",
                req.season(), req.district(), req.state());
    }

    private List<WeeklyForecast> buildWeeklyForecasts(DemandForecastRequest req) {
        List<WeeklyForecast> forecasts = new ArrayList<>();
        BigDecimal basePrice = getBasePriceForCrop(req.cropName());
        int weeks = Math.min(req.forecastWeeks(), 12);
        for (int w = 1; w <= weeks; w++) {
            BigDecimal price = basePrice.add(new BigDecimal(w * 15));
            BigDecimal demand = new BigDecimal(500 + (w * 20));
            forecasts.add(new WeeklyForecast(w, price, demand,
                    w <= 4 ? "Short-term outlook stable" : "Mid-term demand likely to rise"));
        }
        return forecasts;
    }

    private BigDecimal getBasePriceForCrop(String cropName) {
        return switch (cropName.toLowerCase()) {
            case "wheat"   -> new BigDecimal("2200");
            case "rice"    -> new BigDecimal("3800");
            case "maize"   -> new BigDecimal("1900");
            case "onion"   -> new BigDecimal("2500");
            case "tomato"  -> new BigDecimal("1800");
            default        -> new BigDecimal("2000");
        };
    }

    private String determineTrend(String cropName) {
        return switch (cropName.toLowerCase()) {
            case "wheat", "rice"  -> "STABLE";
            case "onion", "garlic"-> "RISING";
            case "tomato"         -> "FALLING";
            default               -> "STABLE";
        };
    }

    private void persistLog(Long farmerId, String type, Object input, Object output) {
        try {
            AdvisoryLog log = new AdvisoryLog();
            log.setFarmerId(farmerId);
            log.setAdvisoryType(type);
            log.setInputJson(objectMapper.writeValueAsString(input));
            log.setOutputJson(objectMapper.writeValueAsString(output));
            advisoryRepo.save(log);
        } catch (JsonProcessingException ignored) {}
    }
}
