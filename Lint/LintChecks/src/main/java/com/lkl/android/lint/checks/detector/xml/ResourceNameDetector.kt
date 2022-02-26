package com.lkl.android.lint.checks.detector.xml

import com.android.SdkConstants
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.lkl.android.lint.checks.bean.ResourceNameRule
import com.lkl.android.lint.checks.config.LintConfig
import com.lkl.android.lint.checks.detector.base.BaseConfigDetector
import com.lkl.android.lint.checks.utils.GsonUtils
import com.lkl.android.lint.checks.utils.LintMatcher
import com.lkl.android.lint.checks.utils.report

/**
 * 检查资源文件命名
 *
 * @author lkl
 * @since 2022/02/15
 */
class ResourceNameDetector : BaseConfigDetector(), XmlScanner {

    companion object {
        private const val KEY_RESOURCE_NAME = "resource_name"

        private const val REPORT_MESSAGE =
            "资源命名请按${LintConfig.CONFIG_FILE_NAME}中${KEY_RESOURCE_NAME}配置的规则"

        val ISSUE = Issue.create(
            "ResourceNameCheck",
            REPORT_MESSAGE,
            REPORT_MESSAGE,
            Category.CORRECTNESS,
            10,
            Severity.ERROR,
            Implementation(ResourceNameDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }

    private var config: ResourceNameRule? = null

    override fun getApplicableElements(): Collection<String>? {
        return listOf(SdkConstants.TAG_RESOURCES)
    }

    override fun beforeCheckRootProject(context: Context) {
        super.beforeCheckRootProject(context)

        config = GsonUtils.parseJson2Obj(customConfig?.toString(), ResourceNameRule::class.java)
    }

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig(KEY_RESOURCE_NAME)
    }

    override fun beforeCheckFile(context: Context) {
        if (context !is XmlContext) {
            return
        }

        val lintConfig = config ?: return

        val resourceName = when (context.resourceFolderType) {
            ResourceFolderType.DRAWABLE -> lintConfig.drawable
            ResourceFolderType.LAYOUT -> lintConfig.layout
            else -> null
        } ?: return

        if (resourceName.name.isEmpty() && resourceName.nameRegex.isEmpty()) {
            return
        }

        val fileName = getBaseName(context.file.name)
        if (!LintMatcher.matchFileName(resourceName, fileName)) {
            context.report(
                ISSUE,
                Location.create(context.file),
                resourceName
            )
        }
    }

}