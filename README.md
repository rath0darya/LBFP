# Laddu Bhai Food Point

Native Android customer app built with Kotlin and Jetpack Compose.

## App details

- **Application ID:** `com.laddufoodpoint`
- **Menu:** 18 configured dishes with Single/Plate pricing where available.
- **Customer flow:** Home, menu search and categories, cart, delivery/pickup checkout, order confirmation, order history and profile/settings.
- **Local persistence:** Cart, customer details, saved addresses and order history are stored on-device.
- **Food preferences:** Optional free-text note entered during checkout. It applies to the current order and is saved in its order details.
- **Appearance:** App-wide dark theme with coordinated backgrounds, cards, inputs, text, navigation and system bars.
- **Branding:** Emerald-and-gold launcher icon and orange food-ordering accent.
- **Release APK:** [Download the latest signed APK](https://github.com/rath0darya/LBFP/releases/download/latest/LBFP-Customer.apk)
- **Checksums:** The release includes `SHA256SUMS.txt` for APK integrity verification.

## Current UI

The home screen uses compact search, photo-led horizontal categories, a concise food hero banner and a fixed four-tab navigation bar. Checkout shows the order summary and offers an optional “Add food preference” text entry below the item list. Food preferences are no longer configured in Settings.

## Integration boundary

This repository does not currently include a restaurant order-management backend, live order tracking, or a payment gateway. Placing an order creates a locally recorded demo order; it does not transmit it to restaurant staff or collect payment. Delivery fee estimates depend on the address and routing service being resolvable. Configure a real, secured backend and payment provider before using the app for live commerce.

## Build and release

Pushes to `main` run the single Android APK workflow. It builds a signed release APK, calculates its SHA-256 checksum, updates the `latest` GitHub Release with build metadata and current feature notes, and replaces both release assets.

Required repository Actions secrets:

- `KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

The workflow uses JDK 17 and Gradle 8.9.
