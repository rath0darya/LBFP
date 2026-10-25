# Laddu Bhai Food Point

Native Android customer app built with Kotlin and Jetpack Compose.

- **Application ID:** `com.laddufoodpoint`
- **Menu:** 18 configured items with the supplied Single/Plate pricing.
- **Customer flow:** Home, menu search and categories, cart, delivery/pickup checkout, order confirmation, order history and profile.
- **Local persistence:** Cart, customer details, food preferences and order history are saved on the device.
- **Branding:** Deep green, cream and gold geometric launcher identity; vegetarian and non-vegetarian menu markers.
- **Release APK:** [Download the latest signed APK](https://github.com/rath0darya/LBFP/releases/download/latest/LBFP-Customer.apk)

## Important integration boundary

This repository does not currently include a restaurant order-management backend, live order tracking, or a payment gateway. Placing an order in the app creates a locally recorded demo order; it does not transmit it to restaurant staff or collect payment. Configure a real, secured backend and payment provider before using the app for live commerce.

## Build and release

Pushes to `main` run the single Android APK workflow. It builds a signed release and replaces the APK and SHA-256 checksum on the GitHub Release tagged `latest`.

Configure these repository Actions secrets:

- `KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

The app uses JDK 17 and Gradle 8.9.
