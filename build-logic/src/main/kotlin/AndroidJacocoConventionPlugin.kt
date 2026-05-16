import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.gyleedev.build_logic.configureJacoco
import com.gyleedev.build_logic.configureJacocoJvm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

class AndroidJacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.gradle.jacoco")
            }

            val androidExtension = extensions.findByType<ApplicationAndroidComponentsExtension>()
                ?: extensions.findByType<LibraryAndroidComponentsExtension>()

            if (androidExtension != null) {
                configureJacoco(androidExtension)
            } else {
                // Android 플러그인이 없는 경우에만 JVM용 Jacoco 설정 적용
                configureJacocoJvm()
            }
        }
    }
}
