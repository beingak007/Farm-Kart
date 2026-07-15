CREATE TABLE IF NOT EXISTS advisory_logs (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    farmer_id      BIGINT,
    advisory_type  VARCHAR(50)  NOT NULL,
    input_json     TEXT,
    output_json    TEXT,
    model_version  VARCHAR(50)  NOT NULL DEFAULT 'rule-based-v1',
    created_at     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_advisory_farmer ON advisory_logs(farmer_id);
CREATE INDEX idx_advisory_type   ON advisory_logs(advisory_type);
