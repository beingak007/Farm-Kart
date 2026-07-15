CREATE TABLE IF NOT EXISTS shipments (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id              BIGINT       NOT NULL,
    logistics_partner_id  BIGINT,
    tracking_number       VARCHAR(50)  UNIQUE,
    pickup_address        TEXT         NOT NULL,
    delivery_address      TEXT         NOT NULL,
    status                VARCHAR(30)  NOT NULL DEFAULT 'CREATED',
    expected_delivery     DATETIME(6),
    actual_delivery       DATETIME(6),
    weight_kg             DECIMAL(10,3),
    notes                 TEXT,
    created_at            DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at            DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_shipments_order_id ON shipments(order_id);
CREATE INDEX idx_shipments_status ON shipments(status);
CREATE INDEX idx_shipments_partner ON shipments(logistics_partner_id);
