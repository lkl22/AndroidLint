package com.lkl.android.lint.checks.detector.source

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.intellij.psi.PsiMethod
import com.lkl.android.lint.checks.detector.base.BaseSourceCodeDetector
import org.jetbrains.uast.UCallExpression


/**
 * 检查启动activity方法的使用
 *
 * @author lkl
 * @since 2022/02/20
 */
@Suppress("UnstableApiUsage")
class StartActivityDetector : BaseSourceCodeDetector() {

    companion object {
        const val CLS_CONTEXT = "android.content.Context"
        const val CLS_FRAGMENT = "androidx.fragment.app.Fragment"

        private const val REPORT_MESSAGE =
            "Do not directly invoke start activity some methods, should use the unified tool class"

        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "StartActivityUsage",
            briefDescription = REPORT_MESSAGE,
            explanation = REPORT_MESSAGE,
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                StartActivityDetector::class.java, Scope.JAVA_FILE_SCOPE
            )
        )
    }

    private var methodNames: List<String>? = null

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("start-activity-usage")
    }

    override fun beforeCheckRootProject(context: Context) {
        super.beforeCheckRootProject(context)

        methodNames = getDetectMethodNames()
    }

    override fun getApplicableMethodNames(): List<String>? {
        return methodNames
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (context.evaluator.isMemberInSubClassOf(
                method, CLS_CONTEXT, false
            ) || context.evaluator.isMemberInSubClassOf(method, CLS_FRAGMENT, false)
        ) {
            val reportMessage = getStringConfig(KEY_REPORT_MESSAGE) ?: REPORT_MESSAGE

            context.report(
                ISSUE, node, context.getLocation(node), reportMessage, getFix(context, node, method)
            )
        }
    }
}
