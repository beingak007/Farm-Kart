package com.farmkart.agent.repository.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Persists an agent chat session header.
 * The actual turn-by-turn history lives in AgentMessage rows.
 */
@Entity
@Table(name = "agent_sessions", indexes = {
    @Index(name = "idx_agent_session_user", columnList = "user_id")
})
public class AgentSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_uuid", unique = true, nullable = false, length = 64)
    private String sessionUuid;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private AgentRole role;

    @Column(name = "turn_count", nullable = false)
    private int turnCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    public enum AgentRole {
        FARMER_ADVISOR, MARKET_ANALYST, LOGISTICS_ASSISTANT, ADMIN_ASSISTANT, GENERAL
    }

    @PrePersist
    void prePersist() {
        createdAt = lastActiveAt = Instant.now();
    }

    // ── Getters/Setters ──────────────────────────────────────────────────────
    public Long getId()                    { return id; }
    public String getSessionUuid()         { return sessionUuid; }
    public void setSessionUuid(String v)   { this.sessionUuid = v; }
    public Long getUserId()                { return userId; }
    public void setUserId(Long v)          { this.userId = v; }
    public AgentRole getRole()             { return role; }
    public void setRole(AgentRole v)       { this.role = v; }
    public int getTurnCount()              { return turnCount; }
    public void setTurnCount(int v)        { this.turnCount = v; }
    public Instant getCreatedAt()          { return createdAt; }
    public Instant getLastActiveAt()       { return lastActiveAt; }
    public void setLastActiveAt(Instant v) { this.lastActiveAt = v; }
}
