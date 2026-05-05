plugins {
    id("gyleedev.android.feature")
}

android {
    namespace = "com.gyleedev.githubsearch.feature.favorite"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))

    implementation(libs.landscapist.glide)
    implementation(libs.landscapist.placeholder)
    implementation(libs.paging.compose)
    implementation(libs.androidx.material.icons)
}
composeCompiler { reportsDestination = layout.buildDirectory.dir("compose_reports") }
