CREATE TABLE IF NOT EXISTS buyers (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL UNIQUE,
    display_name VARCHAR(200) NOT NULL,
    address      TEXT         NOT NULL,
    state        VARCHAR(100) NOT NULL,
    pincode      VARCHAR(10)  NOT NULL,
    gstin        VARCHAR(20),
    company_name VARCHAR(200),
    buyer_type   VARCHAR(30)  NOT NULL DEFAULT 'INDIVIDUAL',
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_buyers_state ON buyers(state);
