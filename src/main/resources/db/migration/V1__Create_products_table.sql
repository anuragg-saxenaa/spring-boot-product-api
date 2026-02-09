-- Migration V1__Create_products_table.sql
-- Create the base products table. Later migrations may add more fields.

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price DOUBLE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
