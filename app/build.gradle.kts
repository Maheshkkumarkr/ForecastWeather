import java.util.Properties

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use(::load)
    }
}

fun readConfig(name: String, envName: String = name): String {
    return (localProperties.getProperty(name)
        ?: System.getenv(envName)
        ?: "").trim().removeSurrounding("\"")
}

val openWeatherApiKey = readConfig("openWeatherApiKey", "OPENWEATHER_API_KEY")
val openWeatherBaseUrl = readConfig("openWeatherBaseUrl", "OPENWEATHER_BASE_URL")
    .ifBlank { "https://api.openweathermap.org/" }

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mahi.weatherapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.mahi.weatherapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "OPENWEATHER_API_KEY", "\"$openWeatherApiKey\"")
        buildConfigField("String", "OPENWEATHER_BASE_URL", "\"$openWeatherBaseUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    coreLibraryDesugaring(libs.android.desugar.jdk.libs)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.koin.androidx.compose)

    // Navigation for Compose
    implementation(libs.bundles.navigation3)
    // Kotlin Serialization JSON
    implementation(libs.kotlinx.serialization.json)
    // WorkManager
    implementation(libs.bundles.workmanager)
    // Ktor HTTP client
    implementation(libs.bundles.ktor.client)
    // Room database
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)
    // Koin dependency injection
    implementation(libs.bundles.koin)
    // Coil image loading
    implementation(libs.bundles.coil)
    // SplashScreen
    implementation(libs.bundles.splashscreen)
    // Lottie animations
    implementation(libs.bundles.lottie)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}