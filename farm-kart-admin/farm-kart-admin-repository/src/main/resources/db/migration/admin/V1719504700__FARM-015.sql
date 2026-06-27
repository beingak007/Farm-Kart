CREATE TABLE IF NOT EXISTS audit_logs (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT,
    service_name  VARCHAR(100) NOT NULL,
    action        VARCHAR(200) NOT NULL,
    resource_type VARCHAR(100),
    resource_id   VARCHAR(100),
    ip_address    VARCHAR(50),
    description   TEXT,
    outcome       VARCHAR(20)  NOT NULL DEFAULT 'SUCCESS',
    created_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_audit_user        ON audit_logs(user_id);
CREATE INDEX idx_audit_service     ON audit_logs(service_name);
CREATE INDEX idx_audit_resource    ON audit_logs(resource_type, resource_id);
CREATE INDEX idx_audit_created_at  ON audit_logs(created_at);
