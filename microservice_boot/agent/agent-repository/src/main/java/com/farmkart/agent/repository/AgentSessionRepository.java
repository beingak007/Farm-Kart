package com.farmkart.agent.repository;

import com.farmkart.agent.repository.entity.AgentSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AgentSessionRepository extends JpaRepository<AgentSession, Long> {
    Optional<AgentSession> findBySessionUuid(String sessionUuid);
}
