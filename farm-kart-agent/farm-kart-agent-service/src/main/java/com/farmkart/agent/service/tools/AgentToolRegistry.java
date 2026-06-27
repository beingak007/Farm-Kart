package com.farmkart.agent.service.tools;

import com.farmkart.agent.service.llm.LlmClient.ToolDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Central registry of all tools the agent can call.
 * Each tool maps a name → (args JSON) → result string.
 *
 * Tools delegate to the individual Farm Kart service REST APIs via WebClient.
 */
@Component
public class AgentToolRegistry {

    private final FarmerToolExecutor     farmerTools;
    private final MarketPriceToolExecutor marketPriceTools;
    private final CatalogToolExecutor    catalogTools;
    private final LogisticsToolExecutor  logisticsTools;
    private final AdvisoryToolExecutor   advisoryTools;

    public AgentToolRegistry(
        FarmerToolExecutor farmerTools,
        MarketPriceToolExecutor marketPriceTools,
        CatalogToolExecutor catalogTools,
        LogisticsToolExecutor logisticsTools,
        AdvisoryToolExecutor advisoryTools
    ) {
        this.farmerTools      = farmerTools;
        this.marketPriceTools = marketPriceTools;
        this.catalogTools     = catalogTools;
        this.logisticsTools   = logisticsTools;
        this.advisoryTools    = advisoryTools;
    }

    public List<ToolDefinition> allDefinitions() {
        return List.of(
            farmerTools.definition(),
            marketPriceTools.definition(),
            catalogTools.definition(),
            logisticsTools.definition(),
            advisoryTools.cropRecommendationDefinition(),
            advisoryTools.demandForecastDefinition()
        );
    }

    public Map<String, Function<String, String>> executors() {
        return Map.of(
            farmerTools.definition().name(),                 farmerTools::execute,
            marketPriceTools.definition().name(),            marketPriceTools::execute,
            catalogTools.definition().name(),                catalogTools::execute,
            logisticsTools.definition().name(),              logisticsTools::execute,
            advisoryTools.cropRecommendationDefinition().name(), advisoryTools::executeCropRecommendation,
            advisoryTools.demandForecastDefinition().name(),     advisoryTools::executeDemandForecast
        );
    }

    /** Execute a named tool with the given arguments JSON. */
    public String execute(String toolName, String argsJson) {
        var exec = executors().get(toolName);
        if (exec == null) {
            return "{\"error\": \"Unknown tool: " + toolName + "\"}";
        }
        try {
            return exec.apply(argsJson);
        } catch (Exception ex) {
            return "{\"error\": \"Tool execution failed: " + ex.getMessage() + "\"}";
        }
    }
}
