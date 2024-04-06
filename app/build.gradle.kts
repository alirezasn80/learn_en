plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("appmetrica-plugin")
    id("com.google.gms.google-services")
    id("kotlinx-serialization")

}

appmetrica {
    postApiKey = { applicationVariant -> "3aadb1b1-5508-44d4-acb9-155531c36fbc" }
    enable = { applicationVariant -> true }    // Optional.
    setMappingBuildTypes(listOf("release"))            // Optional.
    setOffline(false)                            // Optional.
    mappingFile = { applicationVariant -> null }   // Optional.
    enableAnalytics = true                     // Optional.
    allowTwoAppMetricas = { applicationVariant -> false }  // Optional.
}

android {
    namespace = "com.alirezasn80.learn_en"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.alirezasn80.learn_en"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //implementation(libs.common)

    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    //lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)

    // AppMetrica SDK.
    implementation("io.appmetrica.analytics:analytics:6.1.0")
    implementation("io.appmetrica.analytics:push:3.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Poolakey
    implementation("com.github.cafebazaar.Poolakey:poolakey:2.2.0")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //okhttp
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.6")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.6")

    //Translation Scraping
    implementation("org.jsoup:jsoup:1.13.1")

    implementation(libs.kotlinx.serialization.json)

    // To recognize Latin script
    implementation ("com.google.mlkit:text-recognition:16.0.0")
}