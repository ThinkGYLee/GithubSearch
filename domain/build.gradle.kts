plugins {
    id("gyleedev.jvm.library")
}

dependencies {
    api(libs.javax.inject)
    api(libs.paging.common)
    implementation(libs.kotlin.coroutines)
    implementation(libs.google.gson)
}
