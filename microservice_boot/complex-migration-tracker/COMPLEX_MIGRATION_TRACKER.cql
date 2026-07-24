CREATE TABLE IF NOT EXISTS complex_migration_tracker (
    migration_name text,
    task_name text,
    version bigint,
    description text,
    task_type text,
    task_status boolean,
    keyspace_name text,
    database_name text,
    updated_time bigint,
    PRIMARY KEY ((migration_name), task_name)
);
