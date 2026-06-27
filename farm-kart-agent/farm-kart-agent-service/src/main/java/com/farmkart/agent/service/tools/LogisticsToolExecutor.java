package com.farmkart.agent.service.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmkart.agent.service.llm.LlmClient.ToolDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LogisticsToolExecutor {

    @Value("${farmkart.services.logistics-url:http://localhost:8083/logistics-service}")
    private String logisticsUrl;

    private final RestTemplate rest   = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public ToolDefinition definition() {
        return new ToolDefinition(
            "track_shipment",
            "Track a Farm Kart shipment by tracking number. Returns current status and expected delivery date.",
            """
            {
              "type": "object",
              "properties": {
                "trackingNumber": { "type": "string", "description": "Shipment tracking number, e.g. FK-A1B2C3D4" }
              },
              "required": ["trackingNumber"]
            }
            """
        );
    }

    public String execute(String argsJson) {
        try {
            var node = mapper.readTree(argsJson);
            String trackingNumber = node.path("trackingNumber").asText();
            String url  = logisticsUrl + "/api/v1/shipments/track/" + trackingNumber;
            String resp = rest.getForObject(url, String.class);
            return resp != null ? resp : "{\"error\": \"No data\"}";
        } catch (Exception ex) {
            return "{\"error\": \"" + ex.getMessage() + "\"}";
        }
    }
}
