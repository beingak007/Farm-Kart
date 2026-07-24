package com.farmkart.agent.service.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmkart.agent.service.llm.LlmClient.ToolDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AdvisoryToolExecutor {

    @Value("${farmkart.services.ai-advisory-url:http://localhost:8080/farm-kart}")
    private String advisoryUrl;

    private final RestTemplate rest   = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    // ── Crop recommendation ──────────────────────────────────────────────────
    public ToolDefinition cropRecommendationDefinition() {
        return new ToolDefinition(
            "get_crop_recommendations",
            "Get AI-powered crop recommendations based on location, season, and soil type",
            """
            {
              "type": "object",
              "properties": {
                "state":               { "type": "string" },
                "district":            { "type": "string" },
                "season":              { "type": "string", "enum": ["RABI","KHARIF","ZAID"] },
                "soilType":            { "type": "string", "enum": ["CLAY","SANDY","LOAMY","BLACK","RED"] },
                "farmAreaAcres":       { "type": "number" },
                "irrigationAvailable": { "type": "string", "enum": ["YES","NO","PARTIAL"] }
              },
              "required": ["state", "district", "season"]
            }
            """
        );
    }

    public String executeCropRecommendation(String argsJson) {
        return post("/api/v1/ai-advisory/crop-recommendations", argsJson);
    }

    // ── Demand forecast ──────────────────────────────────────────────────────
    public ToolDefinition demandForecastDefinition() {
        return new ToolDefinition(
            "get_demand_forecast",
            "Get AI-powered weekly price and demand forecast for a crop in a state",
            """
            {
              "type": "object",
              "properties": {
                "cropName":      { "type": "string" },
                "state":         { "type": "string" },
                "forecastWeeks": { "type": "integer", "minimum": 1, "maximum": 12, "default": 4 }
              },
              "required": ["cropName"]
            }
            """
        );
    }

    public String executeDemandForecast(String argsJson) {
        return post("/api/v1/ai-advisory/demand-forecast", argsJson);
    }

    private String post(String path, String bodyJson) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<String> resp = rest.exchange(
                advisoryUrl + path, HttpMethod.POST,
                new HttpEntity<>(bodyJson, headers), String.class);
            return resp.getBody() != null ? resp.getBody() : ToolResponseErrors.NO_DATA;
        } catch (Exception ex) {
            return ToolResponseErrors.SERVICE_UNAVAILABLE;
        }
    }
}
