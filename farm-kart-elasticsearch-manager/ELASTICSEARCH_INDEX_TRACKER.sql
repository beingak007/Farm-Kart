CREATE TABLE IF NOT EXISTS elasticsearch_index_tracker (
    service_name     VARCHAR(100) NOT NULL,
    index_name       VARCHAR(200) NOT NULL,
    version          BIGINT       NOT NULL,
    mapping_applied  TINYINT(1)   NOT NULL DEFAULT 0,
    execution_time   BIGINT       NOT NULL,
    mapping_json     JSON,
    PRIMARY KEY (service_name, index_name, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
