CREATE TABLE fk_models (
    id              BIGSERIAL PRIMARY KEY,
    model_name      VARCHAR(100) NOT NULL UNIQUE,
    display_name    VARCHAR(255) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE fk_views (
    id              BIGSERIAL PRIMARY KEY,
    model_id        BIGINT NOT NULL REFERENCES fk_models(id) ON DELETE CASCADE,
    view_name       VARCHAR(100) NOT NULL,
    view_type       VARCHAR(50) NOT NULL DEFAULT 'GRID',
    config          JSONB NOT NULL DEFAULT '{}',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (model_id, view_name)
);

CREATE TABLE fk_view_fields (
    id              BIGSERIAL PRIMARY KEY,
    view_id         BIGINT NOT NULL REFERENCES fk_views(id) ON DELETE CASCADE,
    field_name      VARCHAR(100) NOT NULL,
    display_label   VARCHAR(255) NOT NULL,
    data_type       VARCHAR(50) NOT NULL DEFAULT 'STRING',
    visible         BOOLEAN NOT NULL DEFAULT TRUE,
    searchable      BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order      INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_fk_views_model_id ON fk_views(model_id);
CREATE INDEX idx_fk_view_fields_view_id ON fk_view_fields(view_id);

INSERT INTO fk_models (model_name, display_name, description) VALUES
    ('product', 'Product', 'Marketplace product catalog model'),
    ('vendor', 'Vendor', 'Farmer vendor profile model'),
    ('order', 'Order', 'Buyer order model');

INSERT INTO fk_views (model_id, view_name, view_type) VALUES
    (1, 'product_list', 'GRID'),
    (2, 'vendor_list', 'GRID'),
    (3, 'order_list', 'GRID');

INSERT INTO fk_view_fields (view_id, field_name, display_label, data_type, visible, searchable, sort_order) VALUES
    (1, 'title', 'Product Title', 'STRING', TRUE, TRUE, 1),
    (1, 'price', 'Price', 'DECIMAL', TRUE, FALSE, 2),
    (1, 'stockQuantity', 'Stock', 'DECIMAL', TRUE, FALSE, 3),
    (2, 'farmName', 'Farm Name', 'STRING', TRUE, TRUE, 1),
    (2, 'status', 'Status', 'STRING', TRUE, FALSE, 2),
    (3, 'totalAmount', 'Total', 'DECIMAL', TRUE, FALSE, 1),
    (3, 'status', 'Order Status', 'STRING', TRUE, FALSE, 2);
