plugins {
    `kotlin-dsl`
}

group = "com.gyleedev.build_logic"

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.hilt.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "gyleedev.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "gyleedev.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "gyleedev.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "gyleedev.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "gyleedev.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidRetrofit") {
            id = "gyleedev.android.retrofit"
            implementationClass = "AndroidRetrofitConventionPlugin"
        }
        register("jvmLibrary") {
            id = "gyleedev.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "gyleedev.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidJacoco") {
            id = "gyleedev.android.jacoco"
            implementationClass = "AndroidJacocoConventionPlugin"
        }
    }
}
