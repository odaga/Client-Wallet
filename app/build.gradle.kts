plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("$rootDir/mcash_client.jks")
            storePassword = "XzdKDv@4tD9l"
            keyAlias = "mcashPos"
            keyPassword = "XzdKDv@4tD9l"
        }

        create("staging") {
            storeFile = file("$rootDir/mcash_client.jks")
            storePassword = "XzdKDv@4tD9l"
            keyAlias = "mcashPos"
            keyPassword = "XzdKDv@4tD9l"
        }

    }

    lint {
        baseline = file("lint-baseline.xml")
    }

    compileSdk = 35

    defaultConfig {
        applicationId = "ug.mcash.client"
        minSdk = 27
        versionCode = 1
        versionName = "1.0.0-beta01"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        create("staging") {
            signingConfig = signingConfigs.getByName("staging")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
    namespace = "com.mcash.client"


}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    //implementation(project(path: ':domain'))
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation("com.google.firebase:firebase-crashlytics:19.3.0")
    implementation("com.google.firebase:firebase-analytics:22.1.2")
    implementation("com.google.firebase:firebase-storage:21.0.1")
    implementation("com.google.firebase:firebase-messaging:24.1.0")
    implementation("androidx.activity:activity:1.9.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    //pin field
    implementation("com.github.poovamraj:PinEditTextField:1.2.6")


    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-android-compiler:2.49")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    kapt("androidx.lifecycle:lifecycle-compiler:2.8.7")
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    implementation("androidx.fragment:fragment-ktx:1.8.5")

    //used by viewModels
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    //navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.5")


    //work manager
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.startup:startup-runtime:1.2.0")
    //for worker bringing couldn't instantiate worker
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    //utitlites
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("com.github.tapadoo:alerter:7.2.4")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.hbb20:ccp:2.5.4")
    implementation("io.github.chaosleung:pinview:1.4.4")
    implementation("com.intuit.ssp:ssp-android:1.0.6")
    implementation("com.intuit.sdp:sdp-android:1.0.6")

    implementation(Dependencies.Google.PLAY_SERVICES_LOCATION)
    implementation(Dependencies.Google.PLAY_CORE)

    implementation(Dependencies.Util.COIL)

    implementation(project(":data"))
    implementation(project(":domain"))

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.airbnb.android:lottie:6.6.2")

    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // CameraX
    val cameraCore = "1.4.1"
    implementation("androidx.camera:camera-core:$cameraCore")
    implementation("androidx.camera:camera-camera2:$cameraCore")
    implementation("androidx.camera:camera-lifecycle:$cameraCore")
    implementation("androidx.camera:camera-video:$cameraCore")
    implementation("androidx.camera:camera-extensions:$cameraCore")
    implementation("androidx.camera:camera-view:$cameraCore")

    // ML Kit
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("commons-codec:commons-codec:1.15")
    implementation("com.google.mlkit:face-detection:16.1.7")
    implementation("com.google.mlkit:face-mesh-detection:16.0.0-beta3")

    // Image Compression
    implementation("id.zelory:compressor:3.0.1")



    implementation(project(":data"))
    implementation(project(":domain"))

}
kapt {
    correctErrorTypes = true
}


