# Laddu Bhai Food Point — InfinityFree deployment

- Customer storefront: `/index.php` (no owner/admin links shown to customers).
- Private restaurant owner dashboard: `/owner.php`.
- Website backend and owner management use the InfinityFree MySQL database. Android app source is unchanged.

## Deploy / update

1. Upload the contents of `website/` directly into `htdocs/`.
2. Create the MySQL database in InfinityFree and import `database/schema.sql` if setting up a fresh database.
3. For an existing database, run `database/owner-dashboard-migration.sql` once in phpMyAdmin. It adds GPS coordinates and delivery-person assignment fields. Do not run it twice.
4. Copy `api/shared/config.example.php` to `api/shared/config.local.php` on the server and enter the real MySQL host, database name, username and password. Never commit this private file.
5. Ensure at least one row exists in `owner_users`. Passwords must be generated with PHP `password_hash('your-password', PASSWORD_DEFAULT)`; never store plain text.
6. Visit `/owner.php` and sign in. Keep HTTPS enabled.

## Owner dashboard features

- Customer name, phone, email, delivery address, optional GPS coordinates and food preferences.
- Order number, date, pickup/delivery method, payment preference, each ordered item, quantity, saved unit price, line total, subtotal and exact stored customer total.
- Assign a delivery person manually with their name and phone, add delivery notes, open the customer location in Google Maps, and update order status.
- Filter/search orders and toggle menu item availability.

## Notes

The website saves order prices server-side from the menu price at order time, so the owner dashboard shows the same saved total returned to the customer. Current checkout has no delivery fee, so the displayed delivery fee is ₹0. Online payment is a preference only; no payment gateway is integrated. Customer GPS is optional and only captured when the customer taps the location button and grants browser permission. Customer order lookup by phone alone is not strong identity verification.


## Android customer app integration

The Android app uses `/api/customer/menu.php` for live menu prices and `/api/customer/orders.php` for placing orders. Orders share the MySQL tables used by `/owner/`. Run `database/customer-app-integration.sql` once in phpMyAdmin before testing. It adds serving variants and fee columns and seeds the app menu. The owner menu editor supports editing each variant price.
