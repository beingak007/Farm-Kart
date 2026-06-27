CREATE TABLE IF NOT EXISTS crop_categories (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(200)  NOT NULL UNIQUE,
    slug                VARCHAR(200)  NOT NULL UNIQUE,
    description         TEXT,
    parent_category_id  BIGINT,
    image_url           VARCHAR(500),
    is_active           BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at          DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS crops (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(200)   NOT NULL,
    slug            VARCHAR(200)   NOT NULL UNIQUE,
    category_id     BIGINT         NOT NULL REFERENCES crop_categories(id),
    description     TEXT,
    image_url       VARCHAR(500),
    unit            VARCHAR(20)    NOT NULL DEFAULT 'KG',
    base_price      DECIMAL(12,2)  NOT NULL,
    harvest_season  VARCHAR(100),
    grade_standard  VARCHAR(50),
    is_organic      BOOLEAN        NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at      DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_crops_category ON crops(category_id, is_active);
CREATE INDEX idx_crops_organic  ON crops(is_organic, is_active);
CREATE FULLTEXT INDEX idx_crops_fts ON crops(name, description);

-- Seed categories
INSERT INTO crop_categories (name, slug, description) VALUES
('Cereals & Grains', 'cereals-grains', 'Wheat, Rice, Maize, Millets'),
('Vegetables', 'vegetables', 'Fresh vegetables of all varieties'),
('Fruits', 'fruits', 'Fresh fruits and orchards produce'),
('Pulses & Legumes', 'pulses-legumes', 'Lentils, Chickpeas, Moong, Urad'),
('Oilseeds', 'oilseeds', 'Groundnut, Mustard, Sunflower, Soybean'),
('Spices & Herbs', 'spices-herbs', 'Turmeric, Chilli, Coriander, Cumin'),
('Cash Crops', 'cash-crops', 'Sugarcane, Cotton, Jute, Tobacco');

-- Seed common crops
INSERT INTO crops (name, slug, category_id, unit, base_price, harvest_season) VALUES
('Wheat', 'wheat', 1, 'QUINTAL', 2200.00, 'RABI'),
('Basmati Rice', 'basmati-rice', 1, 'QUINTAL', 3800.00, 'KHARIF'),
('Maize', 'maize', 1, 'QUINTAL', 1900.00, 'KHARIF'),
('Tomato', 'tomato', 2, 'KG', 25.00, 'ALL_YEAR'),
('Onion', 'onion', 2, 'KG', 30.00, 'ALL_YEAR'),
('Potato', 'potato', 2, 'KG', 18.00, 'RABI'),
('Mango (Alphonso)', 'mango-alphonso', 3, 'KG', 120.00, 'SUMMER'),
('Banana', 'banana', 3, 'DOZEN', 45.00, 'ALL_YEAR'),
('Chickpea', 'chickpea', 4, 'QUINTAL', 5200.00, 'RABI'),
('Groundnut', 'groundnut', 5, 'QUINTAL', 4800.00, 'KHARIF'),
('Turmeric', 'turmeric', 6, 'KG', 110.00, 'RABI'),
('Sugarcane', 'sugarcane', 7, 'TON', 3200.00, 'ALL_YEAR');
