plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// Load configuration from the repository root .env file
import java.util.Properties

val envProps = Properties()
val envFile = rootProject.file(".env")
if (envFile.exists()) {
    envFile.inputStream().use { envProps.load(it) }
}

val ftpHost: String = envProps.getProperty("VITE_FTP_HOST", "213.3.5.20")
val ftpUser: String = envProps.getProperty("VITE_FTP_USERNAME", "Wildcam")
val ftpPass: String = envProps.getProperty("VITE_FTP_PASSWORD", "Quickcam_02")
val ftpFolder: String = envProps.getProperty("VITE_FTP_FOLDER", "/")

android {
    namespace = "com.appexsoul.cameraimages"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.appexsoul.cameraimages"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Expose FTP configuration to the app
        buildConfigField("String", "FTP_HOST", "\"$ftpHost\"")
        buildConfigField("String", "FTP_USERNAME", "\"$ftpUser\"")
        buildConfigField("String", "FTP_PASSWORD", "\"$ftpPass\"")
        buildConfigField("String", "FTP_FOLDER", "\"$ftpFolder\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    
    // Additional Material Design Components
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("commons-net:commons-net:3.8.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.airbnb.android:lottie:3.4.0")
}