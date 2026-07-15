package com.farmkart.agent.service.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmkart.agent.service.llm.LlmClient.ToolDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MarketPriceToolExecutor {

    @Value("${farmkart.services.market-price-url:http://localhost:8080/farm-kart}")
    private String marketPriceUrl;

    private final RestTemplate rest   = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public ToolDefinition definition() {
        return new ToolDefinition(
            "get_mandi_prices",
            "Get the latest mandi (wholesale market) prices for a crop in an Indian state",
            """
            {
              "type": "object",
              "properties": {
                "cropName": { "type": "string", "description": "Crop name, e.g. wheat, onion, tomato" },
                "state":    { "type": "string", "description": "Indian state, e.g. Maharashtra, Punjab" }
              },
              "required": ["cropName", "state"]
            }
            """
        );
    }

    public String execute(String argsJson) {
        try {
            var node     = mapper.readTree(argsJson);
            String crop  = node.path("cropName").asText();
            String state = node.path("state").asText();
            String url   = marketPriceUrl + "/api/v1/market-prices/latest?cropName=" + crop + "&state=" + state;
            String response = rest.getForObject(url, String.class);
            return response != null ? response : ToolResponseErrors.NO_DATA;
        } catch (Exception ex) {
            return ToolResponseErrors.SERVICE_UNAVAILABLE;
        }
    }
}
