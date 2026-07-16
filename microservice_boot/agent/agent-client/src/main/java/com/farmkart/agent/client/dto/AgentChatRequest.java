package com.farmkart.agent.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Incoming request body for a single-turn or multi-turn agent chat. */
public record AgentChatRequest(

    /** Optional session ID for multi-turn conversation continuity. Null = new session. */
    String sessionId,

    /** The user's message / question */
    @NotBlank
    @Size(max = 4000)
    String message,

    /** Persona or role context for the agent */
    AgentRole role,

    /**
     * Optional contextual hints the caller already knows about (e.g. current farmer ID).
     * The agent uses these to skip lookup steps when data is already available.
     */
    AgentContext context
) {
    public enum AgentRole {
        FARMER_ADVISOR,
        MARKET_ANALYST,
        LOGISTICS_ASSISTANT,
        ADMIN_ASSISTANT,
        GENERAL
    }
}
