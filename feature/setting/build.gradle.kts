plugins {
    id("gyleedev.android.feature")
}

android {
    namespace = "com.gyleedev.githubsearch.feature.setting"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))

    implementation(libs.appcompat)
    implementation(libs.androidx.material.icons)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
composeCompiler { reportsDestination = layout.buildDirectory.dir("compose_reports") }
