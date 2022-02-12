package com.lkl.android.lint.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.dsl.LintOptions
import com.android.build.gradle.tasks.LintBaseTask
import org.gradle.api.*
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.tasks.TaskState

class MyLintPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        applyTask(project, getAndroidVariants(project))
    }

    private static final String sPluginMisConfiguredErrorMessage = "Plugin requires the 'android' or 'android-library' plugin to be configured.";
    /**
     * get android variant list of the project
     * @param project the compiling project
     * @return android variants
     */
    private static DomainObjectCollection<BaseVariant> getAndroidVariants(Project project) {

        if (project.getPlugins().hasPlugin(AppPlugin)) {
            return project.getPlugins().getPlugin(AppPlugin).extension.applicationVariants
        }

        if (project.getPlugins().hasPlugin(LibraryPlugin)) {
            return project.getPlugins().getPlugin(LibraryPlugin).extension.libraryVariants
        }

        throw new ProjectConfigurationException(sPluginMisConfiguredErrorMessage, null)
    }

    private void applyTask(Project project, DomainObjectCollection<BaseVariant> variants) {
        project.dependencies {
            if (getBooleanProperty(project, "DEBUG_LINT_PLUGIN")) {
                implementation(project.project(':Lint:LintLibrary'))
            } else {
                compile('io.github.lkl22:lintLib:latest.release') {
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

//            def newOptions = new LintOptions()
//            newOptions.lintConfig = lintFile
//            newOptions.warningsAsErrors = true
//            newOptions.abortOnError = true
//            newOptions.htmlReport = true
//            //不放在build下，防止被clean掉
//            newOptions.htmlOutput = project.file("${project.projectDir}/lint-report/lint-report.html")
//            newOptions.xmlReport = false

//            lintTask.lintOptions = newOptions

            lintTask.doFirst {
                if (lintFile.exists()) {
                    lintOldFile = project.file("lintOld.xml")
                    lintFile.renameTo(lintOldFile)
                }
                def isLintXmlReady = copyLintXml(project, lintFile)

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
    boolean copyLintXml(Project project, File targetFile) {

        targetFile.parentFile.mkdirs()

        InputStream lintIns = this.class.getResourceAsStream("/config/lint.xml")
        OutputStream outputStream = new FileOutputStream(targetFile)

        int retrolambdaPluginVersion = getRetrolambdaPluginVersion(project)
        if (retrolambdaPluginVersion >= 180) {
            // 加入屏蔽try with resource 检测  1.8.0版本引入此功能
            InputStream retrolambdaLintIns = this.class.getResourceAsStream("/config/retrolambda_lint.xml")
            XMLMergeUtil.merge(outputStream, "/lint", lintIns, retrolambdaLintIns)
        } else {
            // 未使用 或 使用了不支持try with resource的版本
            IOUtils.copy(lintIns, outputStream)
            IOUtils.closeQuietly(outputStream)
            IOUtils.closeQuietly(lintIns)
        }

        if (targetFile.exists()) {
            return true
        }

        return false
    }
    /**
     * 获取使用的retrolambda plugin版本
     * @param project project
     * @return 没找到时返回-1 ，找到返回正常version
     */
    def static int getRetrolambdaPluginVersion(Project project) {

        DefaultExternalModuleDependency retrolambdaPlugin = findClassPathDependencyVersion(project, 'me.tatarka', 'gradle-retrolambda') as DefaultExternalModuleDependency
        if (retrolambdaPlugin == null) {
            retrolambdaPlugin = findClassPathDependencyVersion(project.getRootProject(), 'me.tatarka', 'gradle-retrolambda') as DefaultExternalModuleDependency
        }
        if (retrolambdaPlugin == null) {
            return -1;
        }
        return retrolambdaPlugin.version.split("-")[0].replaceAll("\\.","").toInteger()
    }

    def static findClassPathDependencyVersion(Project project, group, attributeId) {
        return project.buildscript.configurations.classpath.dependencies.find {
            it.group != null && it.group.equals(group) && it.name.equals(attributeId)
        }
    }

    private boolean getBooleanProperty(Project project, String propertyKey) {
        return project.hasProperty(propertyKey) && Boolean.valueOf(project.property(propertyKey))
    }
}