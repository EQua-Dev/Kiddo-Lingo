plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("com.google.gms.google-services")
    id ("androidx.navigation.safeargs.kotlin")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.awesomenessstudios.schoolprojects.kiddolingo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.awesomenessstudios.schoolprojects.kiddolingo"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("com.google.mlkit:digital-ink-recognition:18.0.0")
//Firebase
    implementation (platform("com.google.firebase:firebase-bom:29.2.1"))
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-core:21.1.1")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.4.1")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")




    implementation ("com.android.support:multidex:1.0.3")

    //Kotlin Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.1.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0-RC")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0-RC")

    //Lottie Animation
    implementation ("com.airbnb.android:lottie:4.2.0")
    implementation ("io.github.amrdeveloper:lottiedialog:1.0.0")

    //Navigation Component
    implementation ("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.6.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.6.0")

    //Data Store
    implementation ("androidx.datastore:datastore-preferences:1.1.0-alpha04")

}