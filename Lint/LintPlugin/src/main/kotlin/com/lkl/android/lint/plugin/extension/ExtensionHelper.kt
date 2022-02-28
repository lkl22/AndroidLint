package com.lkl.android.lint.plugin.extension

import com.lkl.android.lint.plugin.IPlugin
import org.gradle.api.Project

/**
 * 添加扩展属性
 *
 * @author lkl
 * @since 2022/02/28
 */
class ExtensionHelper : IPlugin {
    companion object {
        const val EXTENSION_LINT_CONFIG = "lintConfig"
    }

    override fun apply(project: Project) {
        project.extensions.create(EXTENSION_LINT_CONFIG, LintConfigExtension::class.java)
    }
}