package com.farmkart.agent.service.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenAI Chat Completions API client (GPT-4o / GPT-4-turbo).
 * Activated when farmkart.agent.llm.provider=openai (default).
 */
@Component
@ConditionalOnProperty(name = "farmkart.agent.llm.provider", havingValue = "openai", matchIfMissing = true)
public class OpenAiLlmClient implements LlmClient {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${farmkart.agent.llm.openai.api-key:}")
    private String apiKey;

    @Value("${farmkart.agent.llm.openai.model:gpt-4o-mini}")
    private String model;

    @Value("${farmkart.agent.llm.openai.max-tokens:2048}")
    private int maxTokens;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public LlmResponse chat(List<LlmMessage> messages, List<ToolDefinition> tools) {
        try {
            ObjectNode body = mapper.createObjectNode();
            body.put("model", model);
            body.put("max_tokens", maxTokens);

            ArrayNode msgs = body.putArray("messages");
            for (LlmMessage m : messages) {
                ObjectNode msg = msgs.addObject();
                msg.put("role", m.role());
                msg.put("content", m.content() != null ? m.content() : "");
            }

            if (tools != null && !tools.isEmpty()) {
                ArrayNode toolArr = body.putArray("tools");
                for (ToolDefinition t : tools) {
                    ObjectNode tool = toolArr.addObject();
                    tool.put("type", "function");
                    ObjectNode fn = tool.putObject("function");
                    fn.put("name", t.name());
                    fn.put("description", t.description());
                    fn.set("parameters", mapper.readTree(t.parametersSchema()));
                }
                body.put("tool_choice", "auto");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            ResponseEntity<String> response = restTemplate.exchange(
                API_URL, HttpMethod.POST, new HttpEntity<>(body.toString(), headers), String.class);

            return parseResponse(response.getBody());
        } catch (Exception ex) {
            throw new RuntimeException("LLM call failed: " + ex.getMessage(), ex);
        }
    }

    private LlmResponse parseResponse(String json) throws Exception {
        JsonNode root    = mapper.readTree(json);
        JsonNode choice  = root.path("choices").get(0);
        JsonNode message = choice.path("message");
        String content   = message.path("content").asText(null);
        int tokens       = root.path("usage").path("total_tokens").asInt(0);

        List<ToolCall> toolCalls = new ArrayList<>();
        JsonNode tcNode = message.path("tool_calls");
        if (tcNode.isArray()) {
            for (JsonNode tc : tcNode) {
                toolCalls.add(new ToolCall(
                    tc.path("id").asText(),
                    tc.path("function").path("name").asText(),
                    tc.path("function").path("arguments").asText()
                ));
            }
        }
        return new LlmResponse(content, toolCalls, tokens);
    }
}
