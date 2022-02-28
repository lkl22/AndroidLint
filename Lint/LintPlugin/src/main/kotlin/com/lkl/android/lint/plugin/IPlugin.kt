package com.lkl.android.lint.plugin

import org.gradle.api.Project

/**
 * Plugin
 *
 * @author lkl
 * @since 2022/02/28
 */
interface IPlugin {
    fun apply(project: Project)
}