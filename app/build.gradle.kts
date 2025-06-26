plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.zahid.smartexpensetracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.zahid.smartexpensetracker"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // Firebase ML Kit (OCR)
    //implementation(libs.text.recognition)
    implementation(libs.text.recognition.v1600)

    // Room
    implementation(libs.room.runtime)
    annotationProcessor("androidx.room:room-compiler:2.7.1")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("com.itextpdf:itextg:5.5.10")





    // MPAndroidChart
    //implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // PDF (optional)
   // implementation("com.itextpdf:itext7-core:7.1.15")

    // CSV (optional)
    //implementation("com.opencsv:opencsv:5.7.1")
}