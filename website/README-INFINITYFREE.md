# Laddu Bhai Food Point website — InfinityFree setup

The website has two separate experiences backed by the same InfinityFree MySQL database:

- **Customer storefront:** `/index.php` — menu, cart, checkout and order lookup.
- **Restaurant owner portal:** `/owner/` — secure sign-in, incoming orders and customer/order details, status management, menu editing, price changes and availability controls.

Android source is unchanged. The `/api/customer/*` namespace is reserved for future customer-app integration and is not connected to the Android app yet.

## Deploy on InfinityFree

1. Create an InfinityFree hosting account/domain and enable HTTPS.
2. Create a MySQL database in the control panel and copy the exact host, database name, username and password.
3. Import `database/schema.sql` in phpMyAdmin. The schema creates `owner_users` as well as customer, menu, order and rate-limit tables.
4. Upload the contents of `website/` into `htdocs`, so `index.php`, `owner/`, `assets/`, and `api/` sit directly inside `htdocs`.
5. Copy `api/shared/config.example.php` to `api/shared/config.local.php` on the host. Enter the real database credentials and replace `owner_setup_token` with a private random secret of at least 32 characters. Never commit `config.local.php`.
6. Open `/owner/setup.php` over HTTPS once. Enter the setup token, choose a username (4–80 allowed characters) and a password of at least 12 characters.
7. **Immediately delete `owner/setup.php` from the hosting account** and remove `owner_setup_token` from `config.local.php`. Do not leave the setup page on the public server.
8. Visit `/owner/` and sign in. Test a customer order, update its status, edit a menu price and availability, and verify the customer website reflects the changes. Remove test orders before launch.

## Routes

- `GET /api/site/menu.php` — customer website menu
- `POST /api/site/orders.php` — create a website order
- `GET /api/site/orders.php?phone=...` — recent orders lookup
- `GET /api/customer/menu.php` and `POST/GET /api/customer/orders.php` — separate API namespace reserved for later Android integration
- `/api/owner/manage.php` — owner session API
- `/owner/` — owner dashboard
- `/owner/setup.php` — one-time setup only; delete from hosting immediately after use

## Security and production notes

Customer name, phone, email, address, food preferences, orders and order items are stored in MySQL. Owner actions require a server-side PHP session and CSRF token; passwords are stored as PHP password hashes. Use HTTPS and keep the database config private.

Phone-only customer order lookup is not strong identity verification. Add OTP/customer authentication before exposing sensitive order history publicly. The “Online” payment selection is only a preference; no payment gateway is integrated and the site must not collect card details. Example menu items/prices are placeholders and must be updated before launch.
