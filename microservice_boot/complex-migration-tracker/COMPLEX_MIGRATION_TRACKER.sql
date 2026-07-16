CREATE TABLE IF NOT EXISTS complex_migration_tracker (
    migration_name   VARCHAR(200) NOT NULL,
    task_name        VARCHAR(200) NOT NULL,
    version          BIGINT       NOT NULL,
    description      VARCHAR(500),
    task_type        VARCHAR(20)  NOT NULL,
    task_status      TINYINT(1)   NOT NULL DEFAULT 0,
    keyspace_name    VARCHAR(200),
    database_name    VARCHAR(200),
    updated_time     BIGINT       NOT NULL,
    PRIMARY KEY (migration_name, task_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
