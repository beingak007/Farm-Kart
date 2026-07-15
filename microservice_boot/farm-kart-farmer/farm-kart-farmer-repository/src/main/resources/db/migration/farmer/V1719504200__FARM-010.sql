CREATE TABLE IF NOT EXISTS farmers (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT       NOT NULL UNIQUE,
    farm_name        VARCHAR(200) NOT NULL,
    address          TEXT         NOT NULL,
    state            VARCHAR(100) NOT NULL,
    district         VARCHAR(100) NOT NULL,
    pincode          VARCHAR(10)  NOT NULL,
    farm_area_acres  DECIMAL(10,2),
    primary_crop     VARCHAR(100),
    bank_account     VARCHAR(20),
    ifsc             VARCHAR(15),
    aadhaar_number   VARCHAR(12),
    pan_number       VARCHAR(10),
    status           VARCHAR(30)  NOT NULL DEFAULT 'PENDING_VERIFICATION',
    created_at       DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at       DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_farmers_state       ON farmers(state);
CREATE INDEX idx_farmers_status      ON farmers(status);
CREATE INDEX idx_farmers_state_dist  ON farmers(state, district);
