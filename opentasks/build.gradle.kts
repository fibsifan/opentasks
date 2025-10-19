import com.github.triplet.gradle.play.PlayPublisherExtension
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

plugins {
    id("com.android.application")
    alias(libs.plugins.play.publisher) apply false
}

// commit number is only relevant to the application project
fun gitCommitNo (ref: String): Provider<Int> {
    return providers.exec {
        commandLine("git", "rev-list", "--count", ref)
    }.standardOutput.asText.map { it.trim().toInt() }
}

android {
    val COMPILE_SDK_VERSION: String by project
    compileSdk = COMPILE_SDK_VERSION.toInt()

    defaultConfig {
        val MIN_SDK_VERSION: String by project
        minSdk = MIN_SDK_VERSION.toInt()
        applicationId = "org.dmfs.tasks"

        // spread version code to allow inserting versions if necessary
        val VERSION_OVERRIDE: String by project
        versionCode = gitCommitNo("refs/remotes/origin/main").get() * 99 + gitCommitNo("HEAD").get() + VERSION_OVERRIDE.toInt()
        //versionName = version
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (project.hasProperty("DMFS_RELEASE_KEYSTORE")) {
        signingConfigs {
            /*release {
                val DMFS_RELEASE_KEYSTORE: String by project
                storeFile = file(DMFS_RELEASE_KEYSTORE)
                val DMFS_RELEASE_KEYSTORE_PASSWORD: String by project
                storePassword = DMFS_RELEASE_KEYSTORE_PASSWORD
                val DMFS_RELEASE_KEY_ALIAS: String by project
                keyAlias = DMFS_RELEASE_KEY_ALIAS
                val DMFS_RELEASE_KEY_PASSWORD: String by project
                keyPassword = DMFS_RELEASE_KEY_PASSWORD
            }*/
        }
    }

    buildTypes {
        release {
            if (project.hasProperty("DMFS_RELEASE_KEYSTORE")) {
                //signingConfig(signingConfigs.release)
            }
            isMinifyEnabled = true
            proguardFiles += listOf(file("proguard.cfg"))
        }
    }
    packaging {
        resources {
            excludes += setOf("META-INF/NOTICE", "META-INF/LICENSE")
        }
    }

    productFlavors {
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    namespace = "org.dmfs.tasks"

    lint {
        disable.add("MissingTranslation")
    }
}

dependencies {
    implementation(project(":opentasks-theme"))
    implementation(project(":opentasks-provider"))
    implementation(libs.support.appcompat)
    implementation(libs.support.design)
    implementation(libs.xml.magic) {
        // xmlpull is part of the runtime, so don"t pull it in here
        exclude(group = "xmlpull", module = "xmlpull")
    }
    implementation(libs.android.dashclock)
    implementation(libs.color.picker)
    implementation(libs.codeka.carrot) {
        exclude(module = "iterators") // TODO Remove when iterators have been removed from codeka:carrot
    }
    implementation(libs.android.carrot) {
        exclude(module = "carrot")
        exclude(module = "iterators")
        exclude(module = "jems")
    }
    implementation(libs.jems)
    implementation(libs.datetime)
    implementation(libs.bolts.color)
    implementation(libs.retention.magic)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.jems.testing)

    androidTestImplementation(libs.support.test.runner)
    androidTestImplementation(libs.support.test.rules)
    implementation(project(":opentaskspal"))

    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("androidx.preference:preference:1.1.1")
    implementation("com.maltaisn:recurpicker:2.1.4")
}

if (project.hasProperty("PLAY_STORE_SERVICE_ACCOUNT_CREDENTIALS")) {
    apply(libs.plugins.play.publisher)

    configure<PlayPublisherExtension> {
        val PLAY_STORE_SERVICE_ACCOUNT_CREDENTIALS: String by project
        serviceAccountCredentials = file(PLAY_STORE_SERVICE_ACCOUNT_CREDENTIALS)
        // the track is determined automatically by the version number format

        operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)
        track = when (version.toString()) {
            in Regex("^(\\d+)(\\.\\d+)*(-\\d+-[\\w\\d]+)?-dirty$") -> {
                // work in progress goes to the internal track
                "internal"
            }

            in Regex("^(\\d+)(\\.\\d+)*-\\d+-[\\w\\d]+$") -> {
                // untagged commits go to alpha
                "alpha"
            }

            in Regex("^(\\d+)(\\.\\d+)*$") -> {
                // tagged commits to go beta, from where they get promoted to releases
                "beta"
            }
            else -> throw IllegalArgumentException("Unrecognized version format")
        }
    }
}

tasks.register("postVersion") {
    doLast {
        if (project.hasProperty("OPENTASKS_API_KEY")) {
            val OPENTASKS_API_KEY: String by project
            // publish version number on api.opentasks.app
            val connection = URI.create("https://opentasks-app.appspot.com/v1/app/latest_version/").toURL().openConnection() as HttpURLConnection
            with(connection) {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                setRequestProperty("Authorization", "Token token=\"${OPENTASKS_API_KEY}\"")
                outputStream.use {
                    PrintWriter(it)
                        .write("version_code=${project.android.defaultConfig.versionCode}&version_name=${project.android.defaultConfig.versionName}")
                }
                content
            }
        }
    }
}
