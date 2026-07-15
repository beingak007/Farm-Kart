package com.farmkart.agent.repository.entity;

import jakarta.persistence.*;
import java.time.Instant;

/** One message turn within an agent chat session. */
@Entity
@Table(name = "agent_messages", indexes = {
    @Index(name = "idx_agent_msg_session", columnList = "session_id")
})
public class AgentMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private AgentSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private MessageRole role;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /** JSON array of tool calls made during this turn (nullable for user messages) */
    @Column(name = "tool_calls_json", columnDefinition = "JSON")
    private String toolCallsJson;

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public enum MessageRole { USER, ASSISTANT, TOOL }

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    // ── Getters/Setters ──────────────────────────────────────────────────────
    public Long getId()                    { return id; }
    public AgentSession getSession()       { return session; }
    public void setSession(AgentSession v) { this.session = v; }
    public MessageRole getRole()           { return role; }
    public void setRole(MessageRole v)     { this.role = v; }
    public String getContent()             { return content; }
    public void setContent(String v)       { this.content = v; }
    public String getToolCallsJson()       { return toolCallsJson; }
    public void setToolCallsJson(String v) { this.toolCallsJson = v; }
    public Integer getTokensUsed()         { return tokensUsed; }
    public void setTokensUsed(Integer v)   { this.tokensUsed = v; }
    public Instant getCreatedAt()          { return createdAt; }
}
