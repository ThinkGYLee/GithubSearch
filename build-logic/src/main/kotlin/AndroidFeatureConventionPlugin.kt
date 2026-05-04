import com.android.build.api.dsl.LibraryExtension
import com.gyleedev.build_logic.configureComposeAndroid
import com.gyleedev.build_logic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Feature 모듈에서 공통으로 사용할 설정을 정의하는 컨벤션 플러그인입니다.
 * Library 설정, Compose 설정, Hilt 설정을 모두 포함합니다.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("gyleedev.android.library")
                apply("gyleedev.android.hilt")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            configure<LibraryExtension> {
                configureComposeAndroid(this)
            }

            dependencies {
                add("implementation", libs.findLibrary("hilt-navigation-compose").get())
                add("implementation", libs.findLibrary("navigation-compose").get())
                add("implementation", libs.findLibrary("lifecycle-runtime-compose").get())
                add("implementation", libs.findLibrary("lifecycle-viewmodel-compose").get())
            }
        }
    }
}
