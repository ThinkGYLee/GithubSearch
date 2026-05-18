plugins {
    id("gyleedev.jvm.library")
}

dependencies {
    api(libs.javax.inject)
    api(libs.paging.common)
    implementation(libs.kotlin.coroutines)
    testImplementation(project(":core:testing"))
    testImplementation(libs.androidx.paging.testing)
}
