plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")                                  // ← NUEVO
}

android {
    namespace = "cl.duoc.induztek"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.duoc.induztek"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Íconos de Material (lupa, +, cámara, etc.)
    implementation("androidx.compose.material:material-icons-extended")

    // Navegación entre pantallas
    implementation("androidx.navigation:navigation-compose:2.10.2")

    // Tamaños de pantalla (compact, medium, expanded)
    implementation("androidx.compose.material3:material3-window-size-class")

    // Cámara (CameraX)
    val camerax_version = "1.6.2"
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")


    // Room (SQLite)                                                // ← NUEVO
    val room_version = "2.8.5"                                      // ← NUEVO
    implementation("androidx.room:room-runtime:$room_version")      // ← NUEVO
    implementation("androidx.room:room-ktx:$room_version")          // ← NUEVO
    ksp("androidx.room:room-compiler:$room_version")                // ← NUEVO
}