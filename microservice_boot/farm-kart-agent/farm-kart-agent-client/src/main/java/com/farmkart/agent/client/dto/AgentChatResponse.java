package com.farmkart.agent.client.dto;

import java.time.Instant;
import java.util.List;

/** Response payload returned after one agent turn. */
public record AgentChatResponse(
    String       sessionId,
    String       answer,
    List<ToolCall> toolsUsed,
    int          turnCount,
    Instant      timestamp
) {
    public record ToolCall(
        String toolName,
        String input,
        String output,
        boolean success
    ) {}
}
