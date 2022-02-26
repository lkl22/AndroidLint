package com.lkl.android.lint.checks.detector.source

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.intellij.psi.PsiMethod
import com.lkl.android.lint.checks.bean.MethodParamConfig
import com.lkl.android.lint.checks.bean.MethodParamItem
import com.lkl.android.lint.checks.detector.base.BaseSourceCodeDetector
import com.lkl.android.lint.checks.utils.DetectorUtils
import com.lkl.android.lint.checks.utils.GsonUtils
import org.jetbrains.uast.UCallExpression
import java.util.*


/**
 * 检查方法参数必须为指定的值
 *
 * @author lkl
 * @since 2022/02/21
 */
@Suppress("UnstableApiUsage")
class MethodParamDetector : BaseSourceCodeDetector() {
    companion object {
        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "MethodParamUsage",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Please use the specified parameters.",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = "Please use the specified parameters.", // no need to .trimIndent(), lint does that automatically
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                MethodParamDetector::class.java, Scope.JAVA_FILE_SCOPE
            )
        )
    }

    private var methodParamConfig: MethodParamConfig? = null

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("method-param-usage")
    }

    override fun beforeCheckEachProject(context: Context) {
        super.beforeCheckEachProject(context)

        methodParamConfig =
            GsonUtils.parseJson2Obj(customConfig?.toString(), MethodParamConfig::class.java)
    }

    override fun getApplicableMethodNames(): List<String> {
        return getDetectApiNames()
    }

    private fun getDetectApiNames(): List<String> {
        val methods = ArrayList<String>()
        methodParamConfig?.apply {
            this.methods?.forEach { methodParamItem ->
                if (!methodParamItem.methodName.isNullOrBlank()) {
                    methods.add(methodParamItem.methodName!!)
                }
            }
        }
        return methods
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (methodParamConfig == null) return
        val methodParamItem = getMethodParamItem(context, method) ?: return
        val params = methodParamItem.params ?: return

        val reportMessage = methodParamItem.reportMessage ?: (methodParamConfig?.reportMessage
            ?: "Please use the specified parameters.")

        params.forEach { paramInfo ->
            if (paramInfo.index < 0 || paramInfo.index >= method.parameters.size) {
                return@forEach
            }
            if (node.valueArguments[paramInfo.index]
                    ?.asSourceString() != paramInfo.value
            ) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "$reportMessage\nMethod ${method.name} Param[index=${paramInfo.index}] must set to ${paramInfo.value} "
                )
            }
        }
    }

    private fun getMethodParamItem(
        context: JavaContext, method: PsiMethod
    ): MethodParamItem? {
        methodParamConfig?.apply {
            var methodParamItem: MethodParamItem? = null
            methods?.apply {
                methodParamItem = filterMethodParamItem(this, context, method)
            }
            return methodParamItem
        }
        return null
    }

    private fun filterMethodParamItem(
        apiItems: List<MethodParamItem>, context: JavaContext, method: PsiMethod
    ): MethodParamItem? {
        return apiItems.firstOrNull { methodParamItem: MethodParamItem ->
            if (!DetectorUtils.isBuildVariant(context, methodParamItem.buildVariant)) {
                return@firstOrNull false
            }

            methodParamItem.methodName?.let { methodName ->
                if (methodName == method.name) {
                    methodParamItem.className?.let { className ->
                        context.evaluator.isMemberInSubClassOf(method, className, false)
                    }
                } else {
                    false
                }
            } ?: false
        }
    }
}
