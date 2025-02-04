plugins {
    alias(libs.plugins.runique.android.library)
}

android {
    namespace = "com.adimovska.run.location"
}

dependencies {

    implementation(projects.core.domain)
    implementation(projects.run.domain)

    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

}