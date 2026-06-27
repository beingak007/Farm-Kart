-- Agent Sessions
CREATE TABLE agent_sessions (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_uuid   VARCHAR(64)  NOT NULL UNIQUE,
    user_id        BIGINT,
    role           VARCHAR(30)  NOT NULL DEFAULT 'GENERAL',
    turn_count     INT          NOT NULL DEFAULT 0,
    created_at     DATETIME(6)  NOT NULL,
    last_active_at DATETIME(6)  NOT NULL,
    INDEX idx_agent_session_user (user_id)
) ENGINE=InnoDB;

-- Agent Messages (conversation turns)
CREATE TABLE agent_messages (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id      BIGINT       NOT NULL,
    role            VARCHAR(20)  NOT NULL,
    content         TEXT         NOT NULL,
    tool_calls_json JSON,
    tokens_used     INT,
    created_at      DATETIME(6)  NOT NULL,
    INDEX idx_agent_msg_session (session_id),
    CONSTRAINT fk_agent_msg_session FOREIGN KEY (session_id) REFERENCES agent_sessions(id) ON DELETE CASCADE
) ENGINE=InnoDB;
