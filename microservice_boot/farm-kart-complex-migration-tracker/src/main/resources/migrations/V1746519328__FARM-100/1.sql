-- FARM-100: Add search_keywords column for Elasticsearch sync
ALTER TABLE crops ADD COLUMN IF NOT EXISTS search_keywords VARCHAR(500) NULL;
