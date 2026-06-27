package com.farmkart.agent.client.dto;

/** Optional caller-supplied context injected into the agent system prompt. */
public record AgentContext(
    Long   userId,
    Long   farmerId,
    Long   buyerId,
    String state,
    String district,
    String cropName
) {}
