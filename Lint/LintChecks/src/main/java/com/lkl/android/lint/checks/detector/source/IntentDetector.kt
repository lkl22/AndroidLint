package com.lkl.android.lint.checks.detector.source

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.intellij.psi.PsiMethod
import com.lkl.android.lint.checks.detector.base.BaseSourceCodeDetector
import org.jetbrains.uast.UCallExpression


/**
 * 检查Intent里部分方法的使用
 *
 * @author lkl
 * @since 2022/02/15
 */
@Suppress("UnstableApiUsage")
class IntentDetector : BaseSourceCodeDetector() {

    companion object {
        const val CLS_INTENT = "android.content.Intent"

        private const val REPORT_MESSAGE = "Do not directly invoke $CLS_INTENT this method."

        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "IntentUsage",
            briefDescription = REPORT_MESSAGE,
            explanation = REPORT_MESSAGE,
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                IntentDetector::class.java, Scope.JAVA_FILE_SCOPE
            )
        )
    }

    private var methodNames: List<String>? = null

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("intent-usage")
    }

    override fun beforeCheckRootProject(context: Context) {
        super.beforeCheckRootProject(context)

        methodNames = getDetectMethodNames()
    }

    override fun getApplicableMethodNames(): List<String>? {
        return methodNames
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (context.evaluator.isMemberInClass(method, CLS_INTENT)) {
            val reportMessage = getStringConfig(KEY_REPORT_MESSAGE) ?: REPORT_MESSAGE

            context.report(
                ISSUE, node, context.getLocation(node), reportMessage, getFix(context, node, method)
            )
        }
    }
}
