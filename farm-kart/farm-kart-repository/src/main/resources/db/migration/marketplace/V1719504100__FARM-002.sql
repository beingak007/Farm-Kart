-- Marketplace schema (MySQL) — transactional data

CREATE TABLE users (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    role                VARCHAR(20) NOT NULL DEFAULT 'BUYER',
    name                VARCHAR(255) NOT NULL,
    email               VARCHAR(255) NOT NULL UNIQUE,
    mobile              VARCHAR(20) NOT NULL UNIQUE,
    password_hash       VARCHAR(255),
    is_email_verified   BOOLEAN NOT NULL DEFAULT FALSE,
    is_mobile_verified  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_users_role CHECK (role IN ('BUYER', 'VENDOR', 'ADMIN'))
);

CREATE TABLE refresh_tokens (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    expires_at      TIMESTAMP NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE vendors (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL UNIQUE,
    farm_name           VARCHAR(255) NOT NULL,
    address             TEXT,
    state               VARCHAR(100),
    district            VARCHAR(100),
    pincode             VARCHAR(10),
    bank_account        VARCHAR(50),
    ifsc                VARCHAR(20),
    kyc_document_url    VARCHAR(500),
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_vendor_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

CREATE TABLE categories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    parent_id   BIGINT NULL,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE TABLE products (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id           BIGINT NOT NULL,
    category_id         BIGINT NOT NULL,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    price               DECIMAL(12, 2) NOT NULL,
    unit                VARCHAR(50) NOT NULL,
    stock_quantity      DECIMAL(12, 3) NOT NULL DEFAULT 0,
    min_order_quantity  DECIMAL(12, 3) NOT NULL DEFAULT 1,
    images              JSON NOT NULL DEFAULT (JSON_ARRAY()),
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    is_organic          BOOLEAN NOT NULL DEFAULT FALSE,
    harvest_date        DATE,
    storage_instructions TEXT,
    version             BIGINT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE orders (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id            BIGINT NOT NULL,
    vendor_id           BIGINT NOT NULL,
    total_amount        DECIMAL(12, 2) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'PLACED',
    payment_status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    shipping_address    TEXT NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT NOT NULL,
    product_id      BIGINT NOT NULL,
    quantity        DECIMAL(12, 3) NOT NULL,
    unit_price      DECIMAL(12, 2) NOT NULL,
    subtotal        DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE payments (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id            BIGINT NOT NULL,
    payment_provider    VARCHAR(50) NOT NULL,
    provider_payment_id VARCHAR(255),
    amount              DECIMAL(12, 2) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'INITIATED',
    metadata            JSON NOT NULL DEFAULT (JSON_OBJECT()),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE payouts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id       BIGINT NOT NULL,
    amount          DECIMAL(12, 2) NOT NULL,
    fees            DECIMAL(12, 2) NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    scheduled_date  DATE NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE notifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    type        VARCHAR(100) NOT NULL,
    channel     VARCHAR(10) NOT NULL,
    payload     JSON NOT NULL DEFAULT (JSON_OBJECT()),
    sent_at     TIMESTAMP NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_mobile ON users(mobile);
CREATE INDEX idx_vendors_user_id ON vendors(user_id);
CREATE INDEX idx_products_vendor_id ON products(vendor_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_orders_buyer_id ON orders(buyer_id);
CREATE FULLTEXT INDEX idx_products_title ON products(title);

INSERT INTO categories (name, slug) VALUES
    ('Vegetables', 'vegetables'),
    ('Fruits', 'fruits'),
    ('Grains', 'grains'),
    ('Dairy', 'dairy');
