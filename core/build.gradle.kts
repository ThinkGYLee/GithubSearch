plugins {
    id("gyleedev.android.library")
}

android {
    namespace = "com.gyleedev.core"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
