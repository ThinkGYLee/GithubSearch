plugins {
    id("gyleedev.android.library.compose")
}

android {
    namespace = "com.gyleedev.githubsearch.core.designsystem"
}

dependencies {
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
}
