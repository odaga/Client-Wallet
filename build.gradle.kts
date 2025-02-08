// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("maven.google.com")
        maven("https://jitpack.io")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.5.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.49")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.5")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("maven.google.com")
        maven("https://jitpack.io")
    }
}
//task clean(type: Delete) {
//    delete rootProject.buildDir
//}
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}