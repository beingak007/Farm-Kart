package com.farmkart.agent.repository;

import com.farmkart.agent.repository.entity.AgentMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgentMessageRepository extends JpaRepository<AgentMessage, Long> {
    List<AgentMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
}
