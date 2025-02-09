plugins {
    alias(libs.plugins.runique.android.feature.ui)
    alias(libs.plugins.mapsplatform.secrets.plugin)
}

android {
    namespace = "com.adimovska.run.presentation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.run.domain)

    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.coil)
    implementation(libs.timber)
    implementation(libs.google.maps.android.compose)
}