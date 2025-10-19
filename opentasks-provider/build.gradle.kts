plugins {
    id("com.android.library")
}

android {
    val COMPILE_SDK_VERSION: String by project
    compileSdk = COMPILE_SDK_VERSION.toInt()

    defaultConfig {
        val MIN_SDK_VERSION: String by project
        minSdk = MIN_SDK_VERSION.toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging {
        resources {
            excludes += setOf("META-INF/NOTICE", "META-INF/LICENSE")
        }
    }

    namespace="org.dmfs.tasks.provider"
}

dependencies {
    implementation(project(":opentasks-contract"))

    implementation(libs.datetime)
    implementation(libs.lib.recur)
    implementation(libs.jems)

    testImplementation(libs.robolectric)
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.jems.testing)
    testImplementation(libs.hamcrest)

    androidTestImplementation(project(":opentaskspal"))
    androidTestImplementation(libs.contenttestpal)
    androidTestImplementation(libs.support.annotations)
    androidTestImplementation(libs.support.test.runner)
    androidTestImplementation(libs.support.test.rules)
    androidTestImplementation(libs.mockito)
    androidTestImplementation(libs.jems.testing)
    androidTestImplementation(libs.hamcrest)
    androidTestImplementation(libs.contentpal.testing)
}
