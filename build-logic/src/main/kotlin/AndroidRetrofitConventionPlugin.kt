import com.gyleedev.build_logic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Retrofit 네트워크 통신을 위한 설정을 정의하는 컨벤션 플러그인입니다.
 */
class AndroidRetrofitConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("retrofit2-retrofit").get())
                add("implementation", libs.findLibrary("retrofit2-converter-gson").get())
                add("implementation", libs.findLibrary("okhttp3-logging-interceptor").get())
            }
        }
    }
}
