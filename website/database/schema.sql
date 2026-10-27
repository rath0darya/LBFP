CREATE TABLE IF NOT EXISTS customers (
 id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(100) NOT NULL, phone VARCHAR(16) NOT NULL UNIQUE, email VARCHAR(190) NULL,
 default_address VARCHAR(500) NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL,
 INDEX idx_customers_email(email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE IF NOT EXISTS menu_items (
 id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY, name VARCHAR(140) NOT NULL, category VARCHAR(50) NOT NULL,
 description VARCHAR(500) NOT NULL DEFAULT '', price DECIMAL(10,2) NOT NULL, emoji VARCHAR(20) NOT NULL DEFAULT '🍽️',
 is_available TINYINT(1) NOT NULL DEFAULT 1, sort_order INT NOT NULL DEFAULT 0,
 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 INDEX idx_menu_available(is_available,sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE IF NOT EXISTS orders (
 id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY, order_number VARCHAR(32) NOT NULL UNIQUE,
 customer_id BIGINT UNSIGNED NOT NULL, status VARCHAR(30) NOT NULL DEFAULT 'Received',
 fulfillment ENUM('Pickup','Delivery') NOT NULL, payment_method ENUM('At Restaurant','Online') NOT NULL DEFAULT 'At Restaurant',
 address VARCHAR(500) NOT NULL DEFAULT '', preferences VARCHAR(500) NOT NULL DEFAULT '',
 subtotal DECIMAL(10,2) NOT NULL, total DECIMAL(10,2) NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL,
 CONSTRAINT fk_orders_customer FOREIGN KEY(customer_id) REFERENCES customers(id), INDEX idx_orders_customer_date(customer_id,created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE IF NOT EXISTS order_items (
 id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY, order_id BIGINT UNSIGNED NOT NULL, menu_item_id BIGINT UNSIGNED NOT NULL,
 item_name VARCHAR(140) NOT NULL, unit_price DECIMAL(10,2) NOT NULL, quantity SMALLINT UNSIGNED NOT NULL, line_total DECIMAL(10,2) NOT NULL,
 CONSTRAINT fk_items_order FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE,
 CONSTRAINT fk_items_menu FOREIGN KEY(menu_item_id) REFERENCES menu_items(id), INDEX idx_items_order(order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE IF NOT EXISTS api_rate_limits (
 rate_key CHAR(64) NOT NULL PRIMARY KEY, request_count INT UNSIGNED NOT NULL DEFAULT 0, window_start DATETIME NOT NULL, INDEX idx_rate_window(window_start)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO menu_items(name,category,description,price,emoji,sort_order) VALUES
('Chicken 65','Chicken','Crispy, flavourful chicken bites.',180.00,'🍗',10),
('Chicken Fried Rice','Rice & Noodles','Wok-tossed rice with savoury seasoning.',160.00,'🍚',20),
('Chicken Noodles','Rice & Noodles','Hot noodles tossed with vegetables and chicken.',160.00,'🍜',30),
('Veg Fried Rice','Rice & Noodles','A comforting wok-fried rice favourite.',120.00,'🥘',40),
('Veg Noodles','Rice & Noodles','Noodles tossed with fresh vegetables.',120.00,'🍜',50),
('Paneer 65','Snacks','Crisp paneer bites with a spicy finish.',150.00,'🧀',60),
('Chicken Manchurian','Chicken','A bold, savoury Indo-Chinese favourite.',190.00,'🍲',70),
('Veg Manchurian','Snacks','Vegetable dumplings in a rich sauce.',140.00,'🥢',80)
ON DUPLICATE KEY UPDATE name=VALUES(name);
