CREATE TABLE IF NOT EXISTS mandi_prices (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    crop_name         VARCHAR(100)   NOT NULL,
    mandi_name        VARCHAR(200)   NOT NULL,
    state             VARCHAR(100)   NOT NULL,
    district          VARCHAR(100)   NOT NULL,
    price_per_quintal DECIMAL(10,2)  NOT NULL,
    min_price         DECIMAL(10,2),
    max_price         DECIMAL(10,2),
    price_date        DATE           NOT NULL,
    source            VARCHAR(50)    NOT NULL DEFAULT 'AGMARKNET',
    created_at        DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_mandi_crop_date   ON mandi_prices(crop_name, price_date);
CREATE INDEX idx_mandi_state_crop  ON mandi_prices(state, crop_name);
