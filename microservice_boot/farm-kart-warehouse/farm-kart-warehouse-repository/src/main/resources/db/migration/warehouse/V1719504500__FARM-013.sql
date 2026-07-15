CREATE TABLE IF NOT EXISTS warehouses (
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                     VARCHAR(200) NOT NULL,
    address                  TEXT         NOT NULL,
    state                    VARCHAR(100) NOT NULL,
    district                 VARCHAR(100) NOT NULL,
    pincode                  VARCHAR(10)  NOT NULL,
    total_capacity_tons      DECIMAL(12,3) NOT NULL,
    available_capacity_tons  DECIMAL(12,3) NOT NULL,
    is_cold_storage          BOOLEAN      NOT NULL DEFAULT FALSE,
    status                   VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    owner_id                 BIGINT,
    created_at               DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS warehouse_bookings (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id    BIGINT         NOT NULL REFERENCES warehouses(id),
    farmer_id       BIGINT         NOT NULL,
    crop_name       VARCHAR(100)   NOT NULL,
    quantity_tons   DECIMAL(12,3)  NOT NULL,
    start_date      DATE           NOT NULL,
    end_date        DATE           NOT NULL,
    total_cost      DECIMAL(12,2)  NOT NULL,
    status          VARCHAR(20)    NOT NULL DEFAULT 'CONFIRMED',
    created_at      DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_warehouses_state      ON warehouses(state);
CREATE INDEX idx_warehouses_cold       ON warehouses(is_cold_storage, status);
CREATE INDEX idx_bookings_farmer       ON warehouse_bookings(farmer_id);
CREATE INDEX idx_bookings_warehouse    ON warehouse_bookings(warehouse_id);
