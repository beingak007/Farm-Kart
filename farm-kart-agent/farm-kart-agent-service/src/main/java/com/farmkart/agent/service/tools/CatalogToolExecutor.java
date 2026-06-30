package com.farmkart.agent.service.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmkart.agent.service.llm.LlmClient.ToolDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CatalogToolExecutor {

    @Value("${farmkart.services.catalog-url:http://localhost:8088/catalog-service}")
    private String catalogUrl;

    private final RestTemplate rest   = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public ToolDefinition definition() {
        return new ToolDefinition(
            "search_crops",
            "Search the Farm Kart crop catalog by name or keyword. Returns matching crops with pricing and category information.",
            """
            {
              "type": "object",
              "properties": {
                "query": { "type": "string", "description": "Search term, e.g. 'wheat', 'organic tomato'" },
                "page":  { "type": "integer", "default": 0 },
                "size":  { "type": "integer", "default": 10 }
              },
              "required": ["query"]
            }
            """
        );
    }

    public String execute(String argsJson) {
        try {
            var node    = mapper.readTree(argsJson);
            String q    = node.path("query").asText();
            int page    = node.path("page").asInt(0);
            int size    = node.path("size").asInt(10);
            String url  = catalogUrl + "/api/v1/catalog/crops/search?q=" + q + "&page=" + page + "&size=" + size;
            String resp = rest.getForObject(url, String.class);
            return resp != null ? resp : ToolResponseErrors.NO_DATA;
        } catch (Exception ex) {
            return ToolResponseErrors.SERVICE_UNAVAILABLE;
        }
    }
}
