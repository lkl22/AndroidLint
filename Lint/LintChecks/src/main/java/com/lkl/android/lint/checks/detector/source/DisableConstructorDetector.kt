package com.lkl.android.lint.checks.detector.source

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.intellij.psi.PsiMethod
import com.lkl.android.lint.checks.bean.ConstructorItem
import com.lkl.android.lint.checks.bean.ConstructorUsage
import com.lkl.android.lint.checks.detector.base.BaseSourceCodeDetector
import com.lkl.android.lint.checks.utils.DetectorUtils
import com.lkl.android.lint.checks.utils.GsonUtils
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.getQualifiedName
import java.util.*

/**
 * 检查禁用的构造函数的使用
 *
 * @author lkl
 * @since 2022/02/27
 */
@Suppress("UnstableApiUsage")
class DisableConstructorDetector : BaseSourceCodeDetector() {
    companion object {
        private const val REPORT_MESSAGE = "Do not directly new this class."

        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "DisableConstructorUsage",
            briefDescription = REPORT_MESSAGE,
            explanation = REPORT_MESSAGE,
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                DisableConstructorDetector::class.java, Scope.JAVA_FILE_SCOPE
            )
        )
    }

    private var constructorUsage: ConstructorUsage? = null

    private var constructorTypes: List<String>? = null

    private var issueMap: MutableMap<String, Issue> = HashMap()

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("disable_constructor_usage")
    }

    override fun beforeCheckRootProject(context: Context) {
        super.beforeCheckRootProject(context)

        constructorUsage =
            GsonUtils.parseJson2Obj(customConfig?.toString(), ConstructorUsage::class.java)

        constructorTypes = getDetectConstructorNames()
    }

    override fun getApplicableConstructorTypes(): List<String>? {
        return constructorTypes
    }

    private fun getDetectConstructorNames(): List<String> {
        val methods = ArrayList<String>()
        constructorUsage?.apply {
            constructors?.forEach { item ->
                item.constructorName?.apply {
                    methods.add(this)
                }
            }
        }
        return methods
    }

    override fun visitConstructor(
        context: JavaContext, node: UCallExpression, constructor: PsiMethod
    ) {
        if (constructorUsage == null) return
        val item = getItem(context, node) ?: return

        val reportMessage =
            item.reportMessage ?: (constructorUsage?.reportMessage ?: REPORT_MESSAGE)

        var issue = issueMap[item.constructorName]
        if (issue === null) {
            issue = DetectorUtils.getNewIssue(ISSUE, reportMessage, item.lintSeverity)
            issueMap[item.constructorName!!] = issue
        }

        context.report(
            issue, node, context.getLocation(node), reportMessage
        )
    }

    private fun getItem(
        context: JavaContext, node: UCallExpression
    ): ConstructorItem? {
        constructorUsage?.apply {
            var item: ConstructorItem? = null
            constructors?.apply {
                item = filterItem(this, context, node)
            }
            return item
        }
        return null
    }

    private fun filterItem(
        apiItems: List<ConstructorItem>, context: JavaContext, node: UCallExpression
    ): ConstructorItem? {
        return apiItems.firstOrNull { item ->
            if (!DetectorUtils.isBuildVariant(context, item.buildVariant)) {
                return@firstOrNull false
            }
            item.constructorName?.let { name ->
                name == node.classReference.getQualifiedName()
            } ?: false
        }
    }
}
