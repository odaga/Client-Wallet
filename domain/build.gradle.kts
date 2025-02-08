plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 35

    defaultConfig {
        minSdk = 27

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       // consumerProguardFiles = "consumer-rules.pro"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
            //proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
        create("staging") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
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
    namespace = "mcash.domain"
}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.room:room-common:2.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


//    implementation("com.google.dagger:hilt-android:2.40.1")
//    kapt("com.google.dagger:hilt-android-compiler:2.40.1")

    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-android-compiler:2.49")


    kapt("androidx.lifecycle:lifecycle-compiler:2.8.7")
    implementation("androidx.work:work-runtime-ktx:2.10.0")



    //room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
}

kapt {
    correctErrorTypes = true
}

//plugins {
//    id("com.android.library")
//    kotlin("android")
//    kotlin("kapt")
//    id("kotlin-parcelize")
//    id("dagger.hilt.android.plugin")
//}
//
//android {
//    compileSdk = Dependencies.ProjectConstants.COMPILE_SDK
//
//    defaultConfig {
//        minSdk = Dependencies.ProjectConstants.MINIMUM_SDK
//        targetSdk = Dependencies.ProjectConstants.TARGET_SDK
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")
//    }
//
//    buildTypes {
//        named("debug") {
//        }
//
//        named("release") {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile(
//                    "proguard-android-optimize.txt"
//                ),
//                "proguard-rules.pro"
//            )
//        }
//    }
//
//    flavorDimensions("mcash")
//    productFlavors {
//        register("client") {
//            dimension = "mcash"
//        }
//
//        register("agent") {
//            dimension = "mcash"
//        }
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//
//    kotlinOptions {
//        jvmTarget = "11"
//    }
//}
//
//dependencies {
//
//    implementation(Dependencies.Gradle.KOTLIN_STDLIB)
//
//    testImplementation(Dependencies.Kotlin.COROUTINE_TEST)
//    implementation(Dependencies.Kotlin.COROUTINE_ANDROID)
//
//    implementation(Dependencies.Hilt.HILT_WORKER)
//    implementation(Dependencies.Hilt.HILT_ANDROID)
//    implementation(Dependencies.Hilt.HILT_VIEWMODEL)
//    kapt(Dependencies.Hilt.HILT_ANDROID_COMPILER)
//    kapt(Dependencies.Hilt.HILT_COMPILER)
//    testImplementation(Dependencies.Hilt.HILT_TEST)
//    androidTestImplementation(Dependencies.Hilt.HILT_TEST)
//    kaptAndroidTest(Dependencies.Hilt.HILT_ANDROID_COMPILER)
//
//    testImplementation(Dependencies.Test.TRUTHY)
//    testImplementation(Dependencies.Test.JUNIT)
//    testImplementation(Dependencies.Test.MOCKK)
//    testImplementation(Dependencies.Test.ROBOELECTRIC)
//    androidTestImplementation(Dependencies.Test.TRUTHY)
//    androidTestImplementation(Dependencies.Test.JUNIT_EXT)
//    androidTestImplementation(Dependencies.Test.ESPRESSO)
//    androidTestImplementation(Dependencies.Test.CORE_TESTING)
//    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
//
//    implementation(Dependencies.AndroidX.PAGING)
//    implementation(Dependencies.AndroidX.ROOM_PAGING)
//
//    implementation(Dependencies.Room.RUNTIME)
//    implementation(Dependencies.Room.KTX)
//    kapt(Dependencies.Room.COMPILER)
//
//    implementation(Dependencies.Util.TIMBER)
//}