plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // (optionnel) runner pour tests instrumentés
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // (optionnel mais pratique pour éviter MissingInflatedId à l’avenir)
    // buildFeatures {
    //     viewBinding = true
    // }

    // (optionnel) pour que les tests unitaires puissent accéder à des ressources Android
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Room (Java)
    implementation("androidx.room:room-runtime:2.6.1")
    testImplementation(libs.junit.junit)
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Lifecycle (optionnel)
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.4")

    // --- Firebase ---
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-auth") // requis (FirebaseUI s’appuie dessus)

    // FirebaseUI Auth (optionnel)
    implementation("com.firebaseui:firebase-ui-auth:9.0.0")

    // Facebook SDK (uniquement si nécessaire)
    implementation("com.facebook.android:facebook-android-sdk:16.3.0")

    // --- Tests unitaires (src/test/java) ---
    testImplementation("junit:junit:4.13.2")

    // --- Tests instrumentés (src/androidTest/java) ---
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
