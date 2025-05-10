plugins {
    id("com.android.application")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.cinepass"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cinepass"
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
}


dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //aqui as dependencias do google firebase

    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-firestore:23.0.3")


    //aqui as dependencias do viewpage2

    implementation("androidx.viewpager2:viewpager2:1.0.0")

    //SLIDER
    implementation("com.github.bumptech.glide:glide:4.12.0")


    //
    implementation("com.android.volley:volley:1.2.1")

    //para converter Java Objects para JSON
    implementation ("com.google.code.gson:gson:2.10.1")

    //para gerar o qrcode
    implementation ("com.google.zxing:core:3.4.1")






}