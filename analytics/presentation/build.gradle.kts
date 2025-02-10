plugins {
    alias(libs.plugins.runique.android.feature.ui)
}


android {
    namespace = "com.adimovska.analytics.presentation"
}

dependencies {
    implementation(projects.analytics.domain)
}