import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.properties.hasProperty
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.safeargs)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.gyleedev.githubsearch"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gyleedev.githubsearch"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "CLIENT_ID", "\"${getApiKey("CLIENT_ID")}\"")
        buildConfigField("String", "CLIENT_SECRET", "\"${getApiKey("CLIENT_SECRET")}\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    composeCompiler {
        enableStrongSkippingMode = true
        includeSourceInformation = true
        // composeCompiler 블록내의 설정들은 하단 Reference를 참고해보세요
        // Compose compiler -> Compose compiler options dsl
    }

    buildFeatures {
        compose = true
    }
    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.navigation.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.appcompat)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel.savedstate)

    implementation(libs.fragment)

    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.window.size)
    implementation(libs.androidx.material.icons)
    implementation(libs.androidx.material)
    implementation(libs.google.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    ksp(libs.room.compiler)
    implementation(libs.room.paging)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    implementation(libs.paging.compose)
    implementation(libs.paging.runtime.ktx)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.stdlib)

    implementation(libs.landscapist.glide)
    implementation(libs.landscapist.placeholder)

    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.google.gson)

    implementation(libs.hilt.navigation.compose)

    implementation(libs.androidx.browser)

    implementation(libs.navigation.ui.ktx)
    implementation(libs.navigation.fragment)
}

fun getApiKey(propertyKey: String): String {
    return getProps(propertyKey)
}

@Suppress("UNCHECKED_CAST")
fun <T> getProps(key: String): T {
    val localProps = gradleLocalProperties(rootDir)
    return when {
        localProps.hasProperty(key) -> {
            localProps[key] as T
        }

        project.hasProperty(key) -> {
            project.property(key) as T
        }

        else -> {
            System.getenv(key) as T
        }
    }
}

fun gradleLocalProperties(projectRootDir: File): Properties {
    val properties = Properties()
    val localProperties = File(projectRootDir, "local.properties")

    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    }
    return properties
}
