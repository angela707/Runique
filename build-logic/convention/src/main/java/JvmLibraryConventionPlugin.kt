import com.adimovska.convention.configureKotlinJvm
import com.adimovska.convention.getPluginId
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.apply(getPluginId("jetbrains-kotlin-jvm"))

            configureKotlinJvm()
        }
    }
}