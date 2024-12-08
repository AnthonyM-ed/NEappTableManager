plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.neapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.neapp"
        minSdk = 25
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

    // Room runtime
    implementation ("androidx.room:room-runtime:2.5.1")
    // Room compiler (para generar el código de DAO y demás)
    annotationProcessor ("androidx.room:room-compiler:2.5.1")
    // Opcional: para usar coroutines
    implementation ("androidx.room:room-ktx:2.5.1")
    // Opcional: para soporte de RxJava
    implementation ("androidx.room:room-rxjava2:2.5.1")
    implementation ("com.google.android.material:material:1.9.0")
}