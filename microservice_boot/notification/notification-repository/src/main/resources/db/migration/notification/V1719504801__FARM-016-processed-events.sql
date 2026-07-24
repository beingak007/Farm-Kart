CREATE TABLE IF NOT EXISTS processed_domain_events (
    event_id    VARCHAR(36)  NOT NULL PRIMARY KEY,
    topic       VARCHAR(128) NOT NULL,
    processed_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
