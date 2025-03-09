-- H2 compatible schema

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    inventory INT NOT NULL DEFAULT 0,
    image_path VARCHAR(255),
    category VARCHAR(50) NOT NULL,
    popularity INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Admin user will be created programmatically by DataInitializer

-- Insert sample products
INSERT INTO products (product_name, description, price, inventory, image_path, category, popularity) VALUES
('Calligraphy Brush Set', 'High-quality brush set for beginners', 29.99, 50, '/images/brush-set.jpg', 'brushes', 10),
('Premium Ink', 'Traditional black ink for calligraphy', 15.99, 100, '/images/ink.jpg', 'ink', 8),
('Rice Paper', 'Traditional rice paper for calligraphy practice', 12.99, 200, '/images/rice-paper.jpg', 'paper', 7),
('Calligraphy Art - Harmony', 'Beautiful calligraphy artwork depicting harmony', 199.99, 5, '/images/harmony.jpg', 'calligraphy works', 5);
