package com.farmkart.agent.rest.controller;

import com.farmkart.agent.client.dto.AgentChatRequest;
import com.farmkart.agent.client.dto.AgentChatResponse;
import com.farmkart.agent.service.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API for the Farm Kart ReAct AI Agent.
 *
 * POST /api/v1/agent/chat   → single or multi-turn chat with tool calling
 */
@RestController
@RequestMapping("/api/v1/agent")
@Tag(name = "AI Agent", description = "Farm Kart ReAct AI Agent API")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @Operation(
        summary = "Chat with the Farm Kart AI Agent",
        description = """
            Send a message to the agent. The agent will reason over available tools
            (mandi prices, crop catalog, logistics, AI advisory, etc.) and return a
            grounded, actionable response.

            To continue a conversation, pass the `sessionId` returned in the previous response.
            """
    )
    @PostMapping("/chat")
    public ResponseEntity<AgentChatResponse> chat(@Valid @RequestBody AgentChatRequest request) {
        return ResponseEntity.ok(agentService.chat(request));
    }
}
