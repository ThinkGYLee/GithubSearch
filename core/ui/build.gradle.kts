plugins {
    id("gyleedev.android.library")
}

android {
    namespace = "com.gyleedev.githubsearch.core.ui"
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.fragment)
}
