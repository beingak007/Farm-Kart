CREATE TABLE IF NOT EXISTS report_jobs (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_type    VARCHAR(100)  NOT NULL,
    from_date      DATE          NOT NULL,
    to_date        DATE          NOT NULL,
    filters        TEXT,
    format         VARCHAR(20)   NOT NULL DEFAULT 'JSON',
    status         VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    total_records  BIGINT,
    result_url     VARCHAR(500),
    error_message  TEXT,
    requested_by   BIGINT,
    completed_at   DATETIME(6),
    created_at     DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_reports_type       ON report_jobs(report_type);
CREATE INDEX idx_reports_status     ON report_jobs(status);
CREATE INDEX idx_reports_requested  ON report_jobs(requested_by);
