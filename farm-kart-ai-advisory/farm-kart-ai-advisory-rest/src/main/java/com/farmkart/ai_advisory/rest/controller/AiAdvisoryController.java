package com.farmkart.ai_advisory.rest.controller;

import com.farmkart.ai_advisory.client.dto.CropRecommendationRequest;
import com.farmkart.ai_advisory.client.dto.CropRecommendationResponse;
import com.farmkart.ai_advisory.client.dto.DemandForecastRequest;
import com.farmkart.ai_advisory.client.dto.DemandForecastResponse;
import com.farmkart.ai_advisory.service.AiAdvisoryService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai-advisory")
@Tag(name = "AI Advisory", description = "Crop recommendations, demand forecasting and disease predictions")
public class AiAdvisoryController {

    private final AiAdvisoryService advisoryService;

    public AiAdvisoryController(AiAdvisoryService advisoryService) {
        this.advisoryService = advisoryService;
    }

    @PostMapping("/crop-recommendations")
    @Operation(summary = "Get AI-driven crop recommendations for a farmer")
    public ApiResponse<CropRecommendationResponse> recommend(
            @RequestBody @Valid CropRecommendationRequest req) {
        return ApiResponse.ok(advisoryService.recommendCrops(req));
    }

    @PostMapping("/demand-forecast")
    @Operation(summary = "Get weekly demand and price forecast for a crop")
    public ApiResponse<DemandForecastResponse> forecast(
            @RequestBody @Valid DemandForecastRequest req) {
        return ApiResponse.ok(advisoryService.forecastDemand(req));
    }
}
