plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.entertech.tes.vr"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.entertech.tes.vr"
        minSdk = 24
        targetSdk = 34
        versionCode = 6
        versionName = "0.0.6"

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
}

dependencies {
    implementation(libs.kotlinx.coroutines.core) // 替换为最新版本
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.android) // 替换为最新版本
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx) // 替换为最新版本
    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)  // 替换为最新版本
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
//    implementation(project(":ble-device-tes"))
    implementation (libs.ble.device.tes)
    implementation("cn.entertech.android:base:0.0.5")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.androidx.datastore.preferences)
    implementation (libs.androidx.datastore.core)
    implementation (libs.androidx.datastore)
}