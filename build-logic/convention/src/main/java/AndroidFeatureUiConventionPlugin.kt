import com.adimovska.convention.addUiLayerDependencies
import com.adimovska.convention.getPluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureUiConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply(getPluginId("runique-android-library-compose"))
            }

            dependencies {
                addUiLayerDependencies(target)
            }
        }
    }
}