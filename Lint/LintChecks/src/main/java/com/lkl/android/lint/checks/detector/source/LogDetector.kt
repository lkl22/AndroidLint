package com.lkl.android.lint.checks.detector.source

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.intellij.psi.PsiMethod
import com.lkl.android.lint.checks.detector.base.BaseSourceCodeDetector
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
        private const val REPORT_MESSAGE = "Do not directly invoke android.util.Log methods."

        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "LogUsage",
            briefDescription = REPORT_MESSAGE,
            explanation = REPORT_MESSAGE,
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
            val reportMessage = getStringConfig(KEY_REPORT_MESSAGE) ?: REPORT_MESSAGE

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
