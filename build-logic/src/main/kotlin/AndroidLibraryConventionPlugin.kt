import com.android.build.api.dsl.LibraryExtension
import com.gyleedev.build_logic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * 안드로이드 라이브러리 모듈에서 공통으로 사용할 설정을 정의하는 컨벤션 플러그인입니다.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig {
                    consumerProguardFiles("consumer-rules.pro")
                }
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                }
            }
        }
    }
}
