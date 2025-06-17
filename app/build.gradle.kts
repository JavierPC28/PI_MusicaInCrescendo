plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "org.iesalandalus.pi_musicaincrescendo"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.iesalandalus.pi_musicaincrescendo"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        // Habilita el uso de Jetpack Compose
        compose = true
    }
}

dependencies {
    // System UI Controller para personalizar la barra de estado y navegación
    implementation(libs.accompanist.systemuicontroller)

    // SDK de Google Maps para Compose
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)

    // Extensiones KTX para Core y Activity
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.ktx)

    // Coil para la carga de imágenes asíncrona
    implementation(libs.coil.compose)

    // Bill of Materials (BOM) para Jetpack Compose y dependencias de UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose.v172)
    implementation(libs.androidx.navigation.compose)

    // Componentes de ciclo de vida para ViewModels en Compose
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Firebase (BOM y módulos específicos)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx) // Autenticación
    implementation(libs.firebase.database.ktx) // Realtime Database
    implementation(libs.firebase.storage.ktx) // Cloud Storage
    implementation(libs.androidx.credentials) // Credential Manager
    implementation(libs.androidx.credentials.play.services.auth) // Integración con Google Play Services
    implementation(libs.googleid) // Librería para Google Identity

    // Autenticación con Google Sign-In
    implementation(libs.play.services.auth)

    // Dependencias para testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Dependencias para debug y herramientas de Compose
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}