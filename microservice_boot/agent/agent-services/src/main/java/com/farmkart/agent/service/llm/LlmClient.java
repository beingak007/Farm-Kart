package com.farmkart.agent.service.llm;

import java.util.List;

/**
 * Abstraction over any LLM provider (OpenAI, Anthropic, Ollama, etc.).
 * Implementations inject via Spring @Profile or @ConditionalOnProperty.
 */
public interface LlmClient {

    /** Send a chat history and receive the next assistant message. */
    LlmResponse chat(List<LlmMessage> messages, List<ToolDefinition> tools);

    record LlmMessage(String role, String content, ToolCallResult toolCallResult) {
        public static LlmMessage system(String content) {
            return new LlmMessage("system", content, null);
        }
        public static LlmMessage user(String content) {
            return new LlmMessage("user", content, null);
        }
        public static LlmMessage assistant(String content) {
            return new LlmMessage("assistant", content, null);
        }
        public static LlmMessage toolResult(String toolCallId, String content) {
            return new LlmMessage("tool", content, new ToolCallResult(toolCallId, content));
        }
    }

    record LlmResponse(
        String content,
        List<ToolCall> toolCalls,
        int totalTokens
    ) {
        public boolean hasToolCalls() {
            return toolCalls != null && !toolCalls.isEmpty();
        }
    }

    record ToolCall(String id, String name, String argumentsJson) {}

    record ToolCallResult(String toolCallId, String content) {}

    record ToolDefinition(String name, String description, String parametersSchema) {}
}
