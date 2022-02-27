package com.lkl.android.lint.checks.detector.source

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.lkl.android.lint.checks.bean.InheritItem
import com.lkl.android.lint.checks.bean.InheritUsage
import com.lkl.android.lint.checks.detector.base.BaseSourceCodeDetector
import com.lkl.android.lint.checks.utils.DetectorUtils
import com.lkl.android.lint.checks.utils.GsonUtils
import com.lkl.android.lint.checks.utils.report
import org.jetbrains.uast.*
import java.util.*

/**
 * 检查禁用的构造函数的使用
 *
 * @author lkl
 * @since 2022/02/27
 */
@Suppress("UnstableApiUsage")
class InheritDetector : BaseSourceCodeDetector() {
    companion object {
        private const val REPORT_MESSAGE = "Do not inherit this class."

        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "InheritUsage",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = REPORT_MESSAGE,
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = REPORT_MESSAGE, // no need to .trimIndent(), lint does that automatically
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                InheritDetector::class.java, Scope.JAVA_FILE_SCOPE
            )
        )
    }

    private var superUsage: InheritUsage? = null

    private var superClasses: List<String>? = null

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("inherit_usage")
    }

    override fun beforeCheckRootProject(context: Context) {
        super.beforeCheckRootProject(context)

        superUsage = GsonUtils.parseJson2Obj(customConfig?.toString(), InheritUsage::class.java)

        superClasses = getDetectSuperClasses()
    }

    override fun applicableSuperClasses(): List<String>? {
        return superClasses
    }

    private fun getDetectSuperClasses(): List<String> {
        val methods = ArrayList<String>()
        superUsage?.apply {
            inherits?.forEach { item ->
                item.className?.apply {
                    methods.add(this)
                }
            }
        }
        return methods
    }

    override fun visitClass(context: JavaContext, declaration: UClass) {
        visitHandle(context, declaration)
    }

    override fun visitClass(context: JavaContext, lambda: ULambdaExpression) {
        visitHandle(context, lambda)
    }

    private fun visitHandle(
        context: JavaContext, node: UElement
    ) {
        if (superUsage == null) return
        val item = getItem(context, node) ?: return

        val reportMessage = item.reportMessage ?: (superUsage?.reportMessage ?: REPORT_MESSAGE)

        context.report(
            ISSUE, node, context.getLocation(node), reportMessage, item.lintSeverity, null
        )
    }

    private fun getItem(
        context: JavaContext, node: UElement
    ): InheritItem? {
        superUsage?.apply {
            var item: InheritItem? = null
            inherits?.apply {
                item = filterItem(this, context, node)
            }
            return item
        }
        return null
    }

    private fun filterItem(
        apiItems: List<InheritItem>, context: JavaContext, node: UElement
    ): InheritItem? {
        return apiItems.firstOrNull { item ->
            if (!DetectorUtils.isBuildVariant(context, item.buildVariant)) {
                return@firstOrNull false
            }
            item.className?.let { className ->
                when (node) {
                    is UClass -> {
                        context.evaluator.inheritsFrom(node, className, true)
                    }
                    is ULambdaExpression -> {
                        if (node.functionalInterfaceType is PsiClassReferenceType) {
                            val psiClass =
                                (node.functionalInterfaceType as PsiClassReferenceType).resolve()
                            context.evaluator.inheritsFrom(psiClass, className, false)
                        } else false
                    }
                    else -> false
                }
            } ?: false
        }
    }
}
