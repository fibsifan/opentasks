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
    }
    packagingOptions {
        resources {
            excludes += setOf("META-INF/NOTICE", "META-INF/LICENSE")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    namespace = "org.dmfs.opentaskspal"
}

dependencies {
    api(project(":opentasks-contract"))
    api(libs.contentpal)
    api(libs.datetime)
    api(libs.lib.recur)
    api(libs.support.annotations)
    api(libs.bolts.color)

    implementation(libs.jems)

    testImplementation(libs.contentpal.testing)
    testImplementation(libs.jems.testing)
    testImplementation(libs.robolectric)
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
}