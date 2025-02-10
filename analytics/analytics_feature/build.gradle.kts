plugins {
    alias(libs.plugins.runique.android.dynamic.feature)

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
    api(projects.analytics.presentation)
}