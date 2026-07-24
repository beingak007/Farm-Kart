CREATE TABLE sheet_uploads (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    file_name       VARCHAR(255) NOT NULL,
    original_name   VARCHAR(255) NOT NULL,
    file_type       VARCHAR(50) NOT NULL,
    file_size       BIGINT NOT NULL,
    row_count       INT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'UPLOADED',
    storage_path    VARCHAR(500) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sheet_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_sheet_uploads_user_id ON sheet_uploads(user_id);
