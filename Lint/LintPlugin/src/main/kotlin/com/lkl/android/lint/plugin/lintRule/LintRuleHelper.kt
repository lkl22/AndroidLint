package com.lkl.android.lint.plugin.lintRule

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.tasks.LintBaseTask
import com.lkl.android.lint.plugin.IPlugin
import com.lkl.android.lint.plugin.extension.LintConfigExtension
import com.lkl.android.lint.plugin.utils.IOUtils
import com.lkl.android.lint.plugin.utils.PluginUtils
import com.lkl.android.lint.plugin.utils.XMLMergeUtils
import org.gradle.api.DomainObjectCollection
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.*

class LintRuleHelper : Object(), IPlugin {
    override fun apply(project: Project) {
        applyTask(project, getAndroidVariants(project))
    }

    /**
     * get android variant list of the project
     * @param project the compiling project
     * @return android variants
     */
    private fun getAndroidVariants(project: Project): DomainObjectCollection<BaseVariant>? {
        if (PluginUtils.hasAppPlugin(project)) {
            return project.extensions.getByType(AppExtension::class.java).applicationVariants as DomainObjectCollection<BaseVariant>
        }

        if (PluginUtils.hasLibraryPlugin(project)) {
            return project.extensions.getByType(LibraryExtension::class.java).libraryVariants as DomainObjectCollection<BaseVariant>
        }

        return null
    }

    private fun applyTask(project: Project, variants: DomainObjectCollection<BaseVariant>?) {
        var archonTaskExists = false

        variants?.all { variant ->
            val variantName = variant.name.capitalize()
            val lintTask: LintBaseTask = project.tasks.getByName("lint$variantName") as LintBaseTask

            //Lint 会把project下的lint.xml和lintConfig指定的lint.xml进行合并，为了确保只执行插件中的规则，采取此策略
            val lintFile: File = project.file("lint.xml")
            var lintOldFile: File? = null

            /*
            lintOptions {
               lintConfig file("lint.xml")
               warningsAsErrors true
               abortOnError true
               htmlReport true
               htmlOutput file("lint-report/lint-report.html")
               xmlReport false
            }
            */

            lintTask.doFirst {
                if (lintFile.exists()) {
                    lintOldFile = project.file("lintOld.xml")
                    lintFile.renameTo(lintOldFile)
                }
                val isLintXmlReady = copyLintXml(project, lintOldFile, lintFile)

                if (!isLintXmlReady) {
                    lintOldFile?.renameTo(lintFile)
                    throw GradleException("lint.xml不存在")
                }
            }

            project.gradle.taskGraph.afterTask { task ->
                if (task == lintTask) {
                    lintFile.delete()
                    lintOldFile?.renameTo(lintFile)
                }
            }

            // For archon
            if (!archonTaskExists) {
                archonTaskExists = true
                project.task("lintForArchon").setDependsOn(listOf(lintTask))
            }
        }
    }

    /**
     * copy lint xml
     * @return is lint xml ready
     */
    private fun copyLintXml(project: Project, lintOldFile: File?, targetFile: File): Boolean {
        targetFile.parentFile.mkdirs()

        val lintIns: InputStream = this.getClass().getResourceAsStream("/config/lint.xml")
        val outputStream: OutputStream = FileOutputStream(targetFile)

        val lintConfigExtension = project.extensions.getByType(LintConfigExtension::class.java)
        val lintRuleFile = File(project.rootProject.rootDir, lintConfigExtension.lintRuleFile)
        var lintRuleStream: InputStream? = null
        if (lintRuleFile.exists() && lintRuleFile.isFile) {
            lintRuleStream = FileInputStream(lintRuleFile)
        }

        var oldStream: InputStream? = null
        if (lintOldFile != null) {
            oldStream = FileInputStream(lintOldFile)
        }

        XMLMergeUtils.merge(outputStream, "/lint", lintIns, oldStream, lintRuleStream)

        IOUtils.closeQuietly(outputStream, lintIns, oldStream, lintRuleStream)
        if (targetFile.exists()) {
            return true
        }
        return false
    }
}