package com.lkl.android.lint.plugin.dependency

import com.lkl.android.lint.plugin.IPlugin
import com.lkl.android.lint.plugin.utils.PluginUtils
import org.gradle.api.Project

/**
 * 添加依赖
 *
 * @author lkl
 * @since 2022/02/28
 */
class DependencyHelper : IPlugin {

    companion object {
        const val DEPENDENCY_LINT_PATH = "io.github.lkl22.lint:lintLib:latest.release"
    }

    override fun apply(project: Project) {
        project.configurations.all {
            it.resolutionStrategy.cacheDynamicVersionsFor(0, "seconds")
        }
        if (PluginUtils.getBooleanProperty(project, "DEBUG_LINT_PLUGIN")) {
            project.dependencies.add("implementation", project.project(":Lint:LintLibrary"))
        } else {
            project.dependencies.add("implementation", DEPENDENCY_LINT_PATH)
        }
    }
}