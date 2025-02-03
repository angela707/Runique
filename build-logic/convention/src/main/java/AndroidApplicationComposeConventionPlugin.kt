import com.adimovska.convention.configureAndroidCompose
import com.adimovska.convention.getPluginId
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply(getPluginId("runique-android-application"))
                apply(getPluginId("kotlin-compose"))
            }

            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(commonExtension = extension)
        }
    }
}