package com.lkl.android.lint.checks.detector

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.intellij.psi.PsiMethod
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
        const val CLS_BUNDLE = "android.os.Bundle"
        const val CLS_BASE_BUNDLE = "android.os.BaseBundle"

        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create( // ID: used in @SuppressLint warnings etc
            id = "BundleUsage", // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Do not directly invoke $CLS_BUNDLE some methods.", // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = "Do not directly invoke $CLS_BUNDLE some methods, should use the unified tool class", // no need to .trimIndent(), lint does that automatically
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                BundleDetector::class.java, Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("bundle-usage")
    }

    override fun getApplicableMethodNames(): List<String> {
        return getDetectMethodNames()
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (context.evaluator.isMemberInSubClassOf(method, CLS_BASE_BUNDLE, false)) {
            val reportMessage = getStringConfig(KEY_REPORT_MESSAGE)
                ?: "Do not directly invoke $CLS_BUNDLE some methods."

            context.report(
                ISSUE, node, context.getLocation(node), reportMessage, getFix(context, node, method)
            )
        }
    }
}
