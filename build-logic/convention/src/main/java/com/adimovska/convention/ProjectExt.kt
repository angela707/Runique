package com.adimovska.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")



fun Project.getPluginId(pluginAlias: String): String {
    return libs.findPlugin(pluginAlias)
        .orElseThrow { IllegalArgumentException("Plugin '$pluginAlias' not found in version catalog.") }
        .get()
        .pluginId
}
