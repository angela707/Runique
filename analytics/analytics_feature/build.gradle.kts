plugins {
    alias(libs.plugins.runique.android.dynamic.feature)
    alias(libs.plugins.kotlin.serialization)

}
android {
    namespace = "com.adimovska.analytics.analytics_feature"
}

dependencies {
    implementation(project(":app"))
    implementation(projects.analytics.domain)
    implementation(projects.analytics.data)
    implementation(projects.core.database)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    api(projects.analytics.presentation)
}