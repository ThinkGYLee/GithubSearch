plugins {
    id("gyleedev.android.library")
}

android {
    namespace = "com.gyleedev.core"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
