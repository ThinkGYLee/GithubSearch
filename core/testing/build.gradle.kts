plugins {
    id("gyleedev.jvm.library")
}

dependencies {
    api(project(":domain"))
    api(libs.junit)
    api(libs.mockk)
    api(libs.kotlinx.coroutines.test)
}
