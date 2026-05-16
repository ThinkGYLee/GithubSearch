import com.android.build.api.dsl.LibraryExtension
import com.gyleedev.build_logic.configureKotlinAndroid
import com.gyleedev.build_logic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * 안드로이드 라이브러리 모듈에서 공통으로 사용할 설정을 정의하는 컨벤션 플러그인입니다.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("gyleedev.android.jacoco")
            }

            configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig {
                    consumerProguardFiles("consumer-rules.pro")
                    val versionName = libs.findVersion("app-versionName").get().requiredVersion
                    buildFeatures.buildConfig = true
                    defaultConfig.buildConfigField("String", "VERSION_NAME", "\"$versionName\"")
                }
                buildTypes {
                    getByName("debug") {
                        enableUnitTestCoverage = true
                    }
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                }
            }

            dependencies {
                add("testImplementation", libs.findLibrary("junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())
            }
        }
    }
}
