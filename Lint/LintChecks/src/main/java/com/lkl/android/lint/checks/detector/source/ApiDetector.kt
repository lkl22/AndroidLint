package com.lkl.android.lint.checks.detector.source

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.intellij.psi.PsiMethod
import com.lkl.android.lint.checks.bean.ApiItem
import com.lkl.android.lint.checks.bean.ApiUsage
import com.lkl.android.lint.checks.detector.base.BaseSourceCodeDetector
import com.lkl.android.lint.checks.utils.DetectorUtils
import com.lkl.android.lint.checks.utils.GsonUtils
import org.jetbrains.uast.UCallExpression
import java.util.ArrayList


/**
 * 检查相关方法的使用
 *
 * @author lkl
 * @since 2022/02/20
 */
@Suppress("UnstableApiUsage")
class ApiDetector : BaseSourceCodeDetector() {
    companion object {
        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "ApiUsage",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Do not directly invoke this methods.",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = "Do not directly invoke this methods, should use the unified tool class.", // no need to .trimIndent(), lint does that automatically
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                ApiDetector::class.java, Scope.JAVA_FILE_SCOPE
            )
        )
    }

    private var apiUsage: ApiUsage? = null
    private var methodNames: List<String>? = null

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("api-usage")
    }

    override fun beforeCheckRootProject(context: Context) {
        super.beforeCheckRootProject(context)

        apiUsage = GsonUtils.parseJson2Obj(customConfig?.toString(), ApiUsage::class.java)

        methodNames = getDetectApiNames()
    }

    private fun getDetectApiNames(): List<String> {
        val methods = ArrayList<String>()
        apiUsage?.apply {
            deprecatedMethod?.forEach { apiItem: ApiItem ->
                apiItem.methodNames?.apply {
                    methods.addAll(this)
                }
            }
            handleException?.forEach { apiItem: ApiItem ->
                apiItem.methodNames?.apply {
                    methods.addAll(this)
                }
            }
        }
        return methods
    }

    override fun getApplicableMethodNames(): List<String>? {
        return methodNames
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (apiUsage == null) return
        val apiItem = getApiItem(context, method) ?: return

        fixes = apiItem.fixes

        val reportMessage = apiItem.reportMessage ?: (apiUsage?.reportMessage
            ?: "Do not directly invoke this method.")

        context.report(
            ISSUE, node, context.getLocation(node), reportMessage, getFix(context, node, method)
        )
    }

    private fun getApiItem(
        context: JavaContext, method: PsiMethod
    ): ApiItem? {
        apiUsage?.apply {
            var apiItem: ApiItem? = null
            deprecatedMethod?.apply {
                apiItem = filterApiItem(this, context, method)
            }

            if (apiItem == null) {
                handleException?.apply {
                    apiItem = filterApiItem(this, context, method)
                }
            }
            return apiItem
        }
        return null
    }

    private fun filterApiItem(
        apiItems: List<ApiItem>, context: JavaContext, method: PsiMethod
    ): ApiItem? {
        return apiItems.firstOrNull { apiItem: ApiItem ->
            if (!DetectorUtils.isBuildVariant(context, apiItem.buildVariant)) {
                return@firstOrNull false
            }
            apiItem.className?.let { className ->
                if (context.evaluator.isMemberInSubClassOf(method, className, false)) {
                    apiItem.methodNames?.any { methodName ->
                        methodName == method.name
                    }
                } else {
                    false
                }
            } ?: false
        }
    }
}
