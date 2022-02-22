package com.lkl.android.lint.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.tasks.LintBaseTask
import org.gradle.api.*
import org.gradle.api.tasks.TaskState

class MyLintPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (!hasAndroidPlugin(project)) {
            return
        }

        applyTask(project, getAndroidVariants(project))
    }

    /**
     * get android variant list of the project
     * @param project the compiling project
     * @return android variants
     */
    private static DomainObjectCollection<BaseVariant> getAndroidVariants(Project project) {
        if (hasAppPlugin(project)) {
            return project.extensions.getByName("android").applicationVariants
        }

        if (hasLibraryPlugin(project)) {
            return project.extensions.getByName("android").libraryVariants
        }

        return null
    }

    private void applyTask(Project project, DomainObjectCollection<BaseVariant> variants) {
        project.dependencies {
            if (getBooleanProperty(project, "DEBUG_LINT_PLUGIN")) {
                implementation(project.project(':Lint:LintLibrary'))
            } else {
                compile('io.github.lkl22.lint:lintLib:latest.release') {
                    force = true
                }
            }
        }
        project.configurations.all {
            resolutionStrategy.cacheDynamicVersionsFor 0, 'seconds'
        }

        def archonTaskExists = false

        variants.all { variant ->
            def variantName = variant.name.capitalize()
            LintBaseTask lintTask = project.tasks.getByName("lint" + variantName) as LintBaseTask

            //Lint 会把project下的lint.xml和lintConfig指定的lint.xml进行合并，为了确保只执行插件中的规则，采取此策略
            File lintFile = project.file("lint.xml")
            File lintOldFile = null

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
                def isLintXmlReady = copyLintXml(lintOldFile, lintFile)

                if (!isLintXmlReady) {
                    if (lintOldFile != null) {
                        lintOldFile.renameTo(lintFile)
                    }
                    throw new GradleException("lint.xml不存在")
                }
            }

            project.gradle.taskGraph.afterTask { task, TaskState state ->
                if (task == lintTask) {
                    lintFile.delete()
                    if (lintOldFile != null) {
                        lintOldFile.renameTo(lintFile)
                    }
                }
            }

            // For archon
            if (!archonTaskExists) {
                archonTaskExists = true
                project.task("lintForArchon").dependsOn lintTask
            }

        }
    }
    /**
     * copy lint xml
     * @return is lint xml ready
     */
    boolean copyLintXml(File lintOldFile, File targetFile) {
        targetFile.parentFile.mkdirs()

        InputStream lintIns = this.class.getResourceAsStream("/config/lint.xml")
        OutputStream outputStream = new FileOutputStream(targetFile)

        if (lintOldFile != null) {
            InputStream oldStream = new FileInputStream(lintOldFile)
            XMLMergeUtil.merge(outputStream, "/lint", lintIns, oldStream)
        } else {
            IOUtils.copy(lintIns, outputStream)
        }
        IOUtils.closeQuietly(outputStream, lintIns)
        if (targetFile.exists()) {
            return true
        }
        return false
    }

    static boolean hasAndroidPlugin(Project project) {
        return hasAppPlugin(project) || hasLibraryPlugin(project)
    }

    static boolean hasAppPlugin(Project project) {
        return project.plugins.hasPlugin(AppPlugin)
    }

    static boolean hasLibraryPlugin(Project project) {
        return project.plugins.hasPlugin(LibraryPlugin)
    }

    private static boolean getBooleanProperty(Project project, String propertyKey) {
        return project.hasProperty(propertyKey) && Boolean.valueOf(project.property(propertyKey))
    }
}