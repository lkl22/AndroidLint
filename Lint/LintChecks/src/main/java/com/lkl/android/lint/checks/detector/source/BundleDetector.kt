package com.lkl.android.lint.checks.detector.source

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.intellij.psi.PsiMethod
import com.lkl.android.lint.checks.detector.base.BaseSourceCodeDetector
import org.jetbrains.uast.UCallExpression


/**
 * 检查Bundle里部分方法的使用
 *
 * @author lkl
 * @since 2022/02/17
 */
@Suppress("UnstableApiUsage")
class BundleDetector : BaseSourceCodeDetector() {

    companion object {
        private const val CLS_BUNDLE = "android.os.Bundle"
        const val CLS_BASE_BUNDLE = "android.os.BaseBundle"

        private const val REPORT_MESSAGE = "Do not directly invoke $CLS_BUNDLE this method."

        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "BundleUsage",
            briefDescription = REPORT_MESSAGE,
            explanation = REPORT_MESSAGE,
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                BundleDetector::class.java, Scope.JAVA_FILE_SCOPE
            )
        )
    }

    private var methodNames: List<String>? = null

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("bundle-usage")
    }

    override fun beforeCheckRootProject(context: Context) {
        super.beforeCheckRootProject(context)

        methodNames = getDetectMethodNames()
    }

    override fun getApplicableMethodNames(): List<String>? {
        return methodNames
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (context.evaluator.isMemberInSubClassOf(method, CLS_BASE_BUNDLE, false)) {
            val reportMessage = getStringConfig(KEY_REPORT_MESSAGE) ?: REPORT_MESSAGE

            context.report(
                ISSUE, node, context.getLocation(node), reportMessage, getFix(context, node, method)
            )
        }
    }
}
