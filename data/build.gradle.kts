import com.google.protobuf.gradle.*
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.protobuf") version "0.9.4"
}

val cryptoKeyPropertiesFile = rootProject.file("hidden.properties")
val cryptoKeyProperties = Properties()
cryptoKeyProperties.load(FileInputStream(cryptoKeyPropertiesFile))

android {
    signingConfigs {

    }
    compileSdk = 35

    defaultConfig {
        minSdk = 27

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        named("release") {
            isMinifyEnabled = false

            buildConfigField(
                "String",
                "CLIENT_BASE_URL",
                "${cryptoKeyProperties["CLIENT_BASE_URL"]}"
            )
            buildConfigField(
                "String",
                "CLIENT_VERSION_URL",
                "${cryptoKeyProperties["CLIENT_VERSION_URL"]}"
            )
            buildConfigField(
                "String",
                "CLIENT_KYC_BASE_URL",
                "${cryptoKeyProperties["CLIENT_KYC_BASE_URL"]}"
            )
            buildConfigField("String", "CLIENT_SECRET_KEY", "${cryptoKeyProperties["secretKey"]}")
            buildConfigField("String", "CLIENT_SALT", "${cryptoKeyProperties["salt"]}")
            buildConfigField("String", "CLIENT_IV", "${cryptoKeyProperties["iv"]}")
        }


        named("debug") {

            buildConfigField(
                "String",
                "CLIENT_BASE_URL",
                "${cryptoKeyProperties["CLIENT_DEV_URL"]}"
            )
            buildConfigField(
                "String",
                "CLIENT_VERSION_URL",
                "${cryptoKeyProperties["CLIENT_VERSION_URL"]}"
            )
            buildConfigField("String", "CLIENT_SECRET_KEY", "${cryptoKeyProperties["secretKey"]}")
            buildConfigField("String", "CLIENT_SALT", "${cryptoKeyProperties["salt"]}")
            buildConfigField("String", "CLIENT_IV", "${cryptoKeyProperties["iv"]}")
        }

        create("staging") {
            isMinifyEnabled = true
            buildConfigField(
                "String",
                "CLIENT_BASE_URL",
                "${cryptoKeyProperties["CLIENT_STG_URL"]}"
            )
            buildConfigField(
                "String",
                "CLIENT_VERSION_URL",
                "${cryptoKeyProperties["CLIENT_VERSION_URL"]}"
            )
            buildConfigField("String", "CLIENT_SECRET_KEY", "${cryptoKeyProperties["secretKey"]}")
            buildConfigField("String", "CLIENT_SALT", "${cryptoKeyProperties["salt"]}")
            buildConfigField("String", "CLIENT_IV", "${cryptoKeyProperties["iv"]}")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    namespace = "mcash.data"
}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(project(":domain"))
    implementation("commons-codec:commons-codec:1.15")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


    //data stores
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
//    implementation("com.google.protobuf:protobuf-java:3.11.4")   //adds builders and build extensions to data stores
    implementation("com.google.protobuf:protobuf-java:3.22.3")

    //retrofit, gson, okhttp
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")

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



protobuf {
    protobuf.protoc {
        artifact = if (osdetector.os == "osx") {
            "com.google.protobuf:protoc:3.22.3:osx-x86_64"
        } else {
            "com.google.protobuf:protoc:3.22.3"
        }
    }
    protobuf.generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
            }
        }
    }
}
