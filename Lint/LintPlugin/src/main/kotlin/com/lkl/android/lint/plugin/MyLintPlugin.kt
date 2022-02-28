package com.lkl.android.lint.plugin

import com.lkl.android.lint.plugin.dependency.DependencyHelper
import com.lkl.android.lint.plugin.extension.ExtensionHelper
import com.lkl.android.lint.plugin.lintRule.LintRuleHelper
import com.lkl.android.lint.plugin.utils.PluginUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

class MyLintPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (!PluginUtils.hasAndroidPlugin(project)) {
            return
        }

        ExtensionHelper().apply(project)
        DependencyHelper().apply(project)
        LintRuleHelper().apply(project)
    }
}