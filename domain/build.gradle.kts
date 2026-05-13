plugins {
    id("gyleedev.jvm.library")
}

dependencies {
    api(libs.javax.inject)
    api(libs.paging.common)
    implementation(libs.kotlin.coroutines)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.paging.testing)
}
