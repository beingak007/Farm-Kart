-- PostgreSQL: warehouse app data (photos, ratings, comments, live location trail)
-- warehouse_id references MySQL warehouses.id (logical FK — no cross-DB constraint)

CREATE TABLE IF NOT EXISTS warehouse_photos (
    id            BIGSERIAL PRIMARY KEY,
    warehouse_id  BIGINT       NOT NULL,
    s3_key        VARCHAR(512) NOT NULL,
    caption       VARCHAR(255),
    sort_order    INT          NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_wh_photos_warehouse ON warehouse_photos (warehouse_id);

CREATE TABLE IF NOT EXISTS warehouse_ratings (
    id            BIGSERIAL PRIMARY KEY,
    warehouse_id  BIGINT       NOT NULL,
    farmer_id     BIGINT       NOT NULL,
    stars         SMALLINT     NOT NULL CHECK (stars BETWEEN 1 AND 5),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (warehouse_id, farmer_id)
);

CREATE INDEX idx_wh_ratings_warehouse ON warehouse_ratings (warehouse_id);

CREATE TABLE IF NOT EXISTS warehouse_comments (
    id            BIGSERIAL PRIMARY KEY,
    warehouse_id  BIGINT       NOT NULL,
    farmer_id     BIGINT       NOT NULL,
    rating_id     BIGINT       REFERENCES warehouse_ratings(id) ON DELETE SET NULL,
    body          TEXT         NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_wh_comments_warehouse ON warehouse_comments (warehouse_id);

CREATE TABLE IF NOT EXISTS warehouse_booking_locations (
    id            BIGSERIAL PRIMARY KEY,
    booking_id    BIGINT       NOT NULL,
    warehouse_id  BIGINT       NOT NULL,
    farmer_id     BIGINT       NOT NULL,
    latitude      DECIMAL(9, 6)  NOT NULL,
    longitude     DECIMAL(10, 6) NOT NULL,
    recorded_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_wh_booking_loc_booking ON warehouse_booking_locations (booking_id, recorded_at DESC);

-- Latest location per booking (fast lookup for live map)
CREATE TABLE IF NOT EXISTS warehouse_booking_live (
    booking_id    BIGINT PRIMARY KEY,
    warehouse_id  BIGINT       NOT NULL,
    farmer_id     BIGINT       NOT NULL,
    latitude      DECIMAL(9, 6)  NOT NULL,
    longitude     DECIMAL(10, 6) NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
