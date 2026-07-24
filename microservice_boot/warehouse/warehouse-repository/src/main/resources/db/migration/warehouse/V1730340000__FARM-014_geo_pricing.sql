ALTER TABLE warehouses
    ADD COLUMN latitude DECIMAL(9, 6) NULL,
    ADD COLUMN longitude DECIMAL(10, 6) NULL,
    ADD COLUMN price_per_ton_per_day DECIMAL(10, 2) NOT NULL DEFAULT 5.00;

ALTER TABLE warehouse_bookings
    ADD COLUMN distance_km DECIMAL(8, 2) NULL,
    ADD COLUMN pickup_latitude DECIMAL(9, 6) NULL,
    ADD COLUMN pickup_longitude DECIMAL(10, 6) NULL;

CREATE INDEX idx_warehouses_lat_lng ON warehouses (latitude, longitude);
