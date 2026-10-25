plugins { id("com.android.application"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose") }
android {
 namespace = "com.laddufoodpoint"
 compileSdk = 35
 defaultConfig { applicationId = "com.laddufoodpoint"; minSdk = 24; targetSdk = 35; versionCode = providers.gradleProperty("appVersionCode").orNull?.toInt() ?: 1; versionName = providers.gradleProperty("appVersionName").orNull ?: "1.0" }
 signingConfigs { create("release") { val p = System.getenv("LBFP_KEYSTORE_PATH"); if (!p.isNullOrBlank()) { storeFile = file(p); storePassword = System.getenv("KEYSTORE_PASSWORD"); keyAlias = System.getenv("KEY_ALIAS"); keyPassword = System.getenv("KEY_PASSWORD") } } }
 buildTypes { getByName("release") { isMinifyEnabled = false; signingConfig = signingConfigs.getByName("release") } }
 buildFeatures { compose = true }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 kotlinOptions { jvmTarget = "17" }
}
dependencies { implementation(platform("androidx.compose:compose-bom:2024.12.01")); implementation("androidx.activity:activity-compose:1.10.0"); implementation("androidx.compose.ui:ui"); implementation("androidx.compose.foundation:foundation"); implementation("androidx.compose.material3:material3"); implementation("androidx.compose.material:material-icons-extended"); implementation("io.coil-kt:coil-compose:2.7.0") }
