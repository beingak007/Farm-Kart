ALTER TABLE users
    ADD COLUMN auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL' AFTER role,
    ADD COLUMN provider_user_id VARCHAR(255) NULL AFTER auth_provider,
    MODIFY mobile VARCHAR(20) NULL;

CREATE UNIQUE INDEX idx_users_oauth_provider ON users (auth_provider, provider_user_id);
