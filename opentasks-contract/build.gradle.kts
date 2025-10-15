plugins {
    id("com.android.library")
}

android {
    val COMPILE_SDK_VERSION: String by project
    compileSdk = COMPILE_SDK_VERSION.toInt()

    defaultConfig {
        val MIN_SDK_VERSION: String by project
        minSdk = MIN_SDK_VERSION.toInt()
        val TARGET_SDK_VERSION: String by project
        targetSdk = TARGET_SDK_VERSION.toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    namespace = "org.dmfs.tasks.contract"
}

dependencies {
}
