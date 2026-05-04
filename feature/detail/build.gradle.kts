plugins {
    id("gyleedev.android.feature")
}

android {
    namespace = "com.gyleedev.githubsearch.feature.detail"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))

    implementation(libs.landscapist.glide)
    implementation(libs.landscapist.placeholder)
    implementation(libs.androidx.material.icons)
}
