plugins {
    id("gyleedev.android.library")
    id("gyleedev.android.hilt")
}

android {
    namespace = "com.gyleedev.githubsearch.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
