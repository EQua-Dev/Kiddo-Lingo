// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
//    ext.kotlin_version = "1.5.31"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.2.0")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
        classpath ("com.google.gms:google-services:4.4.2")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")


        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}