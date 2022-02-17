package com.lkl.android.lint.checks.detector

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression


/**
 * 检查系统的Log打印日志类的使用
 *
 * @author lkl
 * @since 2022/02/15
 */
@Suppress("UnstableApiUsage")
class LogDetector : BaseSourceCodeDetector() {
    companion object {
        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "LogUsage",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Do not directly invoke android.util.Log methods.",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = "Do not directly invoke android.util.Log methods, should use the unified tool class", // no need to .trimIndent(), lint does that automatically
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                LogDetector::class.java, Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("log-usage")
    }

    override fun getApplicableMethodNames(): List<String> {
        return listOf("v", "d", "i", "w", "e", "wtf")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (context.evaluator.isMemberInClass(
                method, com.android.tools.lint.checks.LogDetector.LOG_CLS
            )
        ) {
            val reportMessage = getStringConfig(KEY_REPORT_MESSAGE)
                ?: "Do not directly invoke android.util.Log methods."

            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                reportMessage,
                getLogFix(context, node, method)
            )
        }
    }

    private fun getLogFix(
        context: JavaContext, node: UCallExpression, method: PsiMethod
    ): LintFix? {
        val fixItem = fixes?.get(0)
        fixItem?.apply {
            val location = context.getCallLocation(node, true, false)
            return if (className.isNullOrBlank()) null else fix().replace().name(displayName)
                .range(location).with("${className}.${method.name}").shortenNames().autoFix()
                .build()
        }
        return null
    }
}
