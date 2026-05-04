import com.android.build.api.dsl.LibraryExtension
import com.gyleedev.build_logic.configureComposeAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("gyleedev.android.library")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            configure<LibraryExtension> {
                configureComposeAndroid(this)
            }
        }
    }
}
