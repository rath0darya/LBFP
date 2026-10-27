-- Run once in phpMyAdmin after selecting the LBFP database.
-- Adds customer GPS and delivery-person fields required by the owner dashboard.
ALTER TABLE orders
  ADD COLUMN customer_latitude DECIMAL(10,7) NULL AFTER address,
  ADD COLUMN customer_longitude DECIMAL(10,7) NULL AFTER customer_latitude,
  ADD COLUMN delivery_boy_name VARCHAR(100) NULL AFTER customer_longitude,
  ADD COLUMN delivery_boy_phone VARCHAR(16) NULL AFTER delivery_boy_name,
  ADD COLUMN delivery_notes VARCHAR(500) NULL AFTER delivery_boy_phone;
