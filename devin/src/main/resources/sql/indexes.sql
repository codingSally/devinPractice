-- Performance optimization: Add indexes to improve query performance

-- Index for products table
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_price ON products(price);
CREATE INDEX IF NOT EXISTS idx_products_popularity ON products(popularity DESC);
CREATE INDEX IF NOT EXISTS idx_products_inventory ON products(inventory);

-- Index for users table
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Note: In a real application with more tables, additional indexes would be added
-- based on query patterns and performance analysis
