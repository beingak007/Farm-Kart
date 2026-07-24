package com.farmkart.agent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmkart.agent.client.dto.AgentChatRequest;
import com.farmkart.agent.client.dto.AgentChatResponse;
import com.farmkart.agent.repository.AgentMessageRepository;
import com.farmkart.agent.repository.AgentSessionRepository;
import com.farmkart.agent.repository.entity.AgentMessage;
import com.farmkart.agent.repository.entity.AgentSession;
import com.farmkart.agent.service.llm.LlmClient;
import com.farmkart.agent.service.llm.LlmClient.*;
import com.farmkart.agent.service.tools.AgentToolRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ReAct (Reason + Act) agent loop.
 *
 * Each turn:
 *   1. Load conversation history from DB
 *   2. Append user message
 *   3. Call LLM with tool definitions
 *   4. If LLM requests tool calls → execute them → feed results back → repeat
 *   5. When LLM returns final text → persist and return to caller
 *
 * Max iterations per turn is bounded by {@code MAX_TOOL_ITERATIONS} to prevent runaway loops.
 */
@Service
public class AgentService {

    private static final int MAX_TOOL_ITERATIONS = 8;

    private final AgentSessionRepository sessionRepo;
    private final AgentMessageRepository messageRepo;
    private final LlmClient              llm;
    private final AgentToolRegistry      tools;
    private final ObjectMapper           mapper = new ObjectMapper();

    public AgentService(
        AgentSessionRepository sessionRepo,
        AgentMessageRepository messageRepo,
        LlmClient llm,
        AgentToolRegistry tools
    ) {
        this.sessionRepo = sessionRepo;
        this.messageRepo = messageRepo;
        this.llm         = llm;
        this.tools       = tools;
    }

    @Transactional
    public AgentChatResponse chat(AgentChatRequest req) {
        // ── Resolve or create session ─────────────────────────────────────────
        AgentSession session = resolveSession(req);

        // ── Build LLM message history ─────────────────────────────────────────
        List<LlmMessage> history = new ArrayList<>();
        history.add(LlmMessage.system(systemPrompt(req, session)));

        List<AgentMessage> pastMessages = messageRepo.findBySessionIdOrderByCreatedAtAsc(session.getId());
        for (AgentMessage m : pastMessages) {
            history.add(new LlmMessage(
                m.getRole().name().toLowerCase(),
                m.getContent(),
                null
            ));
        }
        history.add(LlmMessage.user(req.message()));

        // ── Persist user message ──────────────────────────────────────────────
        persist(session, AgentMessage.MessageRole.USER, req.message(), null, null);

        // ── ReAct loop ────────────────────────────────────────────────────────
        List<AgentChatResponse.ToolCall> toolsUsed = new ArrayList<>();
        List<ToolDefinition> toolDefs = tools.allDefinitions();

        LlmResponse llmResponse = null;
        for (int i = 0; i < MAX_TOOL_ITERATIONS; i++) {
            llmResponse = llm.chat(history, toolDefs);

            if (!llmResponse.hasToolCalls()) {
                break;
            }

            // Execute each requested tool and append results to history
            for (ToolCall tc : llmResponse.toolCalls()) {
                String result = tools.execute(tc.name(), tc.argumentsJson());
                toolsUsed.add(new AgentChatResponse.ToolCall(tc.name(), tc.argumentsJson(), result, !result.contains("\"error\"")));

                history.add(new LlmMessage("assistant",
                    "Calling tool: " + tc.name() + " with args: " + tc.argumentsJson(), null));
                history.add(LlmMessage.toolResult(tc.id(), result));
            }
        }

        String answer = llmResponse != null && llmResponse.content() != null
            ? llmResponse.content()
            : "I was unable to complete the request after executing available tools.";

        // ── Persist assistant response ────────────────────────────────────────
        String toolCallJson = toolsUsed.isEmpty() ? null : toJson(toolsUsed);
        persist(session, AgentMessage.MessageRole.ASSISTANT, answer, toolCallJson,
            llmResponse != null ? llmResponse.totalTokens() : null);

        session.setTurnCount(session.getTurnCount() + 1);
        session.setLastActiveAt(Instant.now());
        sessionRepo.save(session);

        return new AgentChatResponse(
            session.getSessionUuid(),
            answer,
            toolsUsed,
            session.getTurnCount(),
            Instant.now()
        );
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private AgentSession resolveSession(AgentChatRequest req) {
        if (req.sessionId() != null && !req.sessionId().isBlank()) {
            return sessionRepo.findBySessionUuid(req.sessionId())
                .orElseGet(() -> createSession(req));
        }
        return createSession(req);
    }

    private AgentSession createSession(AgentChatRequest req) {
        AgentSession s = new AgentSession();
        s.setSessionUuid(UUID.randomUUID().toString());
        s.setUserId(req.context() != null ? req.context().userId() : null);
        s.setRole(mapRole(req.role()));
        return sessionRepo.save(s);
    }

    private AgentSession.AgentRole mapRole(AgentChatRequest.AgentRole r) {
        if (r == null) return AgentSession.AgentRole.GENERAL;
        return switch (r) {
            case FARMER_ADVISOR      -> AgentSession.AgentRole.FARMER_ADVISOR;
            case MARKET_ANALYST      -> AgentSession.AgentRole.MARKET_ANALYST;
            case LOGISTICS_ASSISTANT -> AgentSession.AgentRole.LOGISTICS_ASSISTANT;
            case ADMIN_ASSISTANT     -> AgentSession.AgentRole.ADMIN_ASSISTANT;
            default                  -> AgentSession.AgentRole.GENERAL;
        };
    }

    private String systemPrompt(AgentChatRequest req, AgentSession session) {
        String role = switch (session.getRole()) {
            case FARMER_ADVISOR      -> "You are a Farm Kart AI agricultural advisor helping Indian farmers with crop planning, market prices, and logistics.";
            case MARKET_ANALYST      -> "You are a Farm Kart market intelligence analyst providing insights on crop prices, demand trends, and trading opportunities.";
            case LOGISTICS_ASSISTANT -> "You are a Farm Kart logistics assistant helping with shipment tracking, warehouse selection, and delivery planning.";
            case ADMIN_ASSISTANT     -> "You are a Farm Kart admin assistant helping with platform management, reports, and user oversight.";
            default                  -> "You are a Farm Kart AI assistant helping farmers and buyers on the digital agricultural marketplace.";
        };

        StringBuilder sb = new StringBuilder(role);
        sb.append("\n\nYou have access to tools to query real-time data from the Farm Kart platform.");
        sb.append("\nAlways use tools to fetch current data before making recommendations.");
        sb.append("\nRespond in clear, simple language. Be concise and actionable.");

        if (req.context() != null) {
            var ctx = req.context();
            sb.append("\n\nUser context:");
            if (ctx.farmerId() != null) sb.append("\n- Farmer ID: ").append(ctx.farmerId());
            if (ctx.state()    != null) sb.append("\n- State: ").append(ctx.state());
            if (ctx.district() != null) sb.append("\n- District: ").append(ctx.district());
            if (ctx.cropName() != null) sb.append("\n- Primary crop: ").append(ctx.cropName());
        }
        return sb.toString();
    }

    private void persist(AgentSession session, AgentMessage.MessageRole role,
                         String content, String toolCallsJson, Integer tokens) {
        AgentMessage m = new AgentMessage();
        m.setSession(session);
        m.setRole(role);
        m.setContent(content);
        m.setToolCallsJson(toolCallsJson);
        m.setTokensUsed(tokens);
        messageRepo.save(m);
    }

    private String toJson(Object obj) {
        try { return mapper.writeValueAsString(obj); }
        catch (Exception ex) { return "[]"; }
    }
}
