# Laddu Bhai Food Point website — InfinityFree setup

This is a website/backend-only change. Android source code is not modified. The website-facing API routes (/api/site/*) and future customer-app routes (/api/customer/*) are separate paths, but share PHP handlers and the same MySQL database.

## Deployment
1. Create an InfinityFree hosting account/domain and enable HTTPS.
2. Create a MySQL database in the InfinityFree control panel; copy the exact hostname, database name, username and password.
3. Import database/schema.sql into that database using phpMyAdmin.
4. Upload the contents of the website directory to htdocs so index.php, assets/ and api/ sit directly inside htdocs.
5. Copy api/shared/config.example.php to api/shared/config.local.php on the host and fill in your database credentials. Never commit config.local.php.
6. Visit the HTTPS site, check that the menu loads, then submit a test order and verify customers, orders and order_items in phpMyAdmin. Delete test records before launch.

## Routes
- GET /api/site/menu.php — website menu
- POST /api/site/orders.php — create website order
- GET /api/site/orders.php?phone=... — recent orders lookup
- GET /api/customer/menu.php and POST/GET /api/customer/orders.php — separate customer-app route namespace for later integration

The Android app is intentionally not connected to these routes in this commit. Connecting it requires a separate, explicitly requested app change.

## Data and safety
Customer name, phone, email, address, food preferences, order headers and item lines are stored in MySQL. The cart only exists in page memory; no customer data is saved to browser storage. Prices and availability are re-read from MySQL on order submission. PHP uses PDO prepared statements, input validation, request-size limits, same-origin checks for writes, basic database-backed rate limiting, and generic database error messages. Configure config.local.php privately on the host. Example menu items/prices are placeholders; update them in phpMyAdmin before launch. Phone-only order lookup is not strong identity verification; add OTP or proper authentication before exposing sensitive order history in production. Do not collect card data on this site.
