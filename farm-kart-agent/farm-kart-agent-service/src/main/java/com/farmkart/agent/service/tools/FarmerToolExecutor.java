package com.farmkart.agent.service.tools;

import com.farmkart.agent.service.llm.LlmClient.ToolDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FarmerToolExecutor {

    @Value("${farmkart.services.farmer-url:http://localhost:8081/farmer-service}")
    private String farmerUrl;

    private final RestTemplate rest = new RestTemplate();

    public ToolDefinition definition() {
        return new ToolDefinition(
            "get_farmer_profile",
            "Get a farmer's profile, farm details, and verification status by their user ID",
            """
            {
              "type": "object",
              "properties": {
                "userId": { "type": "integer", "description": "Farmer's platform user ID" }
              },
              "required": ["userId"]
            }
            """
        );
    }

    public String execute(String argsJson) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            long userId = mapper.readTree(argsJson).path("userId").asLong();
            String response = rest.getForObject(
                farmerUrl + "/api/v1/farmers/user/" + userId, String.class);
            return response != null ? response : ToolResponseErrors.NO_DATA;
        } catch (Exception ex) {
            return ToolResponseErrors.SERVICE_UNAVAILABLE;
        }
    }
}
