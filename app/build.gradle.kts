import org.jetbrains.kotlin.konan.properties.hasProperty
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs")
    id("kotlin-parcelize")
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
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
    buildFeatures {
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    val navVersion = "2.7.7"

    implementation("androidx.navigation:navigation-compose:$navVersion")

    // lifecycle 관련 라이브러리를 2.8.0으로 올리려면 compose version도 1.7.0 이상으로 세팅(현재 1.6.7)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")

    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.fragment:fragment-ktx:1.7.1")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3-window-size-class")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-paging:2.6.1")
    implementation("androidx.paging:paging-runtime-ktx:3.3.0")
    implementation("androidx.paging:paging-compose:3.3.0")

    implementation("androidx.recyclerview:recyclerview:1.3.2")

    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-android-compiler:2.48")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")

    implementation("com.github.skydoves:landscapist-glide:2.3.3")
    implementation("com.github.skydoves:landscapist-placeholder:2.3.3")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.browser:browser:1.8.0")
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
