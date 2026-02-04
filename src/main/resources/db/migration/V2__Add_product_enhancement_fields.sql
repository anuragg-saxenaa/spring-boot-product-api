-- Migration V2__Add_product_enhancement_fields.sql
-- Add new fields to existing products table without breaking existing data

-- Add new columns with default values to maintain backward compatibility
ALTER TABLE products 
ADD COLUMN IF NOT EXISTS category VARCHAR(50),
ADD COLUMN IF NOT EXISTS stock_quantity INTEGER DEFAULT 0,
ADD COLUMN IF NOT EXISTS sku VARCHAR(50) UNIQUE,
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

-- Create index on SKU for better performance
CREATE INDEX IF NOT EXISTS idx_products_sku ON products(sku);

-- Create index on category for better search performance  
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);

-- Create index on active status for filtering
CREATE INDEX IF NOT EXISTS idx_products_active ON products(is_active);

-- Price history table (new table, no migration needed)
CREATE TABLE IF NOT EXISTS price_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    old_price DOUBLE NOT NULL,
    new_price DOUBLE NOT NULL,
    change_reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);