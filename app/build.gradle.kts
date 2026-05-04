import com.android.build.api.dsl.ApplicationExtension
import com.gyleedev.build_logic.getApiKey

plugins {
    id("gyleedev.android.application")
    id("gyleedev.android.hilt")
    alias(libs.plugins.safeargs)
    alias(libs.plugins.compose.compiler)
}

extensions.configure<ApplicationExtension>("android") {
    namespace = "com.gyleedev.githubsearch"

    defaultConfig {
        applicationId = "com.gyleedev.githubsearch"
        buildConfigField("String", "CLIENT_ID", "\"${getApiKey("CLIENT_ID")}\"")
        buildConfigField("String", "CLIENT_SECRET", "\"${getApiKey("CLIENT_SECRET")}\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":feature:home"))

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

    implementation(libs.paging.compose)

    implementation(libs.kotlin.coroutines)

    implementation(libs.landscapist.glide)
    implementation(libs.landscapist.placeholder)

    implementation(libs.hilt.navigation.compose)

    implementation(libs.androidx.browser)

    implementation(libs.navigation.ui.ktx)
    implementation(libs.navigation.fragment)
}
