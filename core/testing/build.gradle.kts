plugins {
    id("gyleedev.jvm.library")
}

dependencies {
    api(libs.junit)
    api(libs.mockk)
    api(libs.kotlinx.coroutines.test)
}
