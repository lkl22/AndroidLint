package com.lkl.android.lint.plugin.utils

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Project

object PluginUtils {
    fun hasAndroidPlugin(project: Project): Boolean {
        return hasAppPlugin(project) || hasLibraryPlugin(project)
    }

    fun hasAppPlugin(project: Project): Boolean {
        return project.plugins.hasPlugin(AppPlugin::class.java)
    }

    fun hasLibraryPlugin(project: Project): Boolean {
        return project.plugins.hasPlugin(LibraryPlugin::class.java)
    }

    fun getBooleanProperty(project: Project, propertyKey: String): Boolean {
        return project.hasProperty(propertyKey) && java.lang.Boolean.valueOf(
            project.property(
                propertyKey
            ) as String
        )
    }
}