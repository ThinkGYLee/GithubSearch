import com.android.build.api.dsl.ApplicationExtension
import com.gyleedev.build_logic.configureKotlinAndroid
import com.gyleedev.build_logic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * 안드로이드 애플리케이션 모듈(:app)에서 공통으로 사용할 설정을 정의하는 컨벤션 플러그인입니다.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("gyleedev.android.jacoco")
            }

            configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.findVersion("project-targetSdk").get().requiredVersion.toInt()
                defaultConfig.versionCode = libs.findVersion("app-versionCode").get().requiredVersion.toInt()
                defaultConfig.versionName = libs.findVersion("app-versionName").get().requiredVersion

                buildTypes {
                    getByName("debug") {
                        enableUnitTestCoverage = true
                    }
                    getByName("release") {
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
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
