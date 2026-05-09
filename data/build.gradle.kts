import com.gyleedev.build_logic.getApiKey

plugins {
    id("gyleedev.android.library")
    id("gyleedev.android.room")
    id("gyleedev.android.retrofit")
    id("gyleedev.android.hilt")
}

android {
    namespace = "com.gyleedev.data"
    buildFeatures {
        buildConfig = true
    }
    // app 모듈에 정의된 getApiKey 로직을 사용하여 필드 추가
    defaultConfig {
        buildConfigField("String", "CLIENT_ID", "\"${getApiKey("CLIENT_ID")}\"")
        buildConfigField("String", "CLIENT_SECRET", "\"${getApiKey("CLIENT_SECRET")}\"")
        buildConfigField("String", "API_URL", "\"https://api.github.com\"")
        buildConfigField("String", "ACCESS_URL", "\"https://github.com\"")
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(libs.room.paging)
    implementation(libs.paging.runtime.ktx)
    implementation(libs.kotlin.coroutines)
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
