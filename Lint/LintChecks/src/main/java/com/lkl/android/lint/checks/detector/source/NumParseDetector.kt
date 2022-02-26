package com.lkl.android.lint.checks.detector.source

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.intellij.psi.PsiMethod
import com.lkl.android.lint.checks.detector.base.BaseSourceCodeDetector
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.java.JavaULiteralExpression
import org.jetbrains.uast.kotlin.KotlinStringULiteralExpression


/**
 * 数字字符串解析为num类型检测
 *
 * @author lkl
 * @since 2022/02/15
 */
@Suppress("UnstableApiUsage")
class NumParseDetector : BaseSourceCodeDetector() {
    companion object {
        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create( // ID: used in @SuppressLint warnings etc
            id = "NumParseUsage", // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Num parse maybe throw NumberFormatException.", // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = "Num parse maybe throw NumberFormatException, should try catch.",
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                NumParseDetector::class.java, Scope.JAVA_FILE_SCOPE
            )
        )

        const val CLS_NUMBER = "java.lang.Number"
        const val CLS_BYTE = "java.lang.Byte"
        const val CLS_SHORT = "java.lang.Short"
        const val CLS_INTEGER = "java.lang.Integer"
        const val CLS_LONG = "java.lang.Long"
        const val CLS_Float = "java.lang.Float"
        const val CLS_Double = "java.lang.Double"

        const val METHOD_VALUE_OF = "valueOf"
        const val METHOD_PARSE_BYTE = "parseByte"
        const val METHOD_PARSE_SHORT = "parseShort"
        const val METHOD_PARSE_INT = "parseInt"
        const val METHOD_PARSE_LONG = "parseLong"
        const val METHOD_PARSE_FLOAT = "parseFloat"
        const val METHOD_PARSE_DOUBLE = "parseDouble"
    }

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("num-parse-usage")
    }

    override fun getApplicableMethodNames(): List<String> {
        return listOf(
            METHOD_VALUE_OF,
            METHOD_PARSE_BYTE,
            METHOD_PARSE_SHORT,
            METHOD_PARSE_INT,
            METHOD_PARSE_LONG,
            METHOD_PARSE_FLOAT,
            METHOD_PARSE_DOUBLE
        )
    }

    private var curClsName: String? = null

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (context.evaluator.isMemberInSubClassOf(method, CLS_NUMBER, true)) {
            curClsName = method.containingClass?.qualifiedName
            when (val firstParamExp = node.valueArguments[0]) {
                is JavaULiteralExpression -> {
                    validParam(context, node, method, firstParamExp.value as String, curClsName)
                }
                is KotlinStringULiteralExpression -> {
                    validParam(context, node, method, firstParamExp.value, curClsName)
                }
                else -> {
                    handleNonConstParam(context, node, method)
                }
            }
        }
    }

    private fun handleNonConstParam(
        context: JavaContext, node: UCallExpression, method: PsiMethod
    ) {
        val reportMsg = getStringConfig(KEY_REPORT_MESSAGE) ?: "Please use the unified tool class"
        report(context, node, reportMsg, getFix(context, node, method))
    }

    override fun getNewMethodName(methodMap: Map<String, String>?, methodName: String): String {
        val newMethodName =
            if (methodName == METHOD_VALUE_OF) getParseMethod(curClsName) else methodName
        return methodMap?.get(newMethodName) ?: newMethodName
    }

    private fun validParam(
        context: JavaContext,
        node: UCallExpression,
        method: PsiMethod,
        numParam: String,
        clsName: String?
    ) {
        try {
            when (clsName) {
                CLS_BYTE -> numParam.toByte()
                CLS_SHORT -> numParam.toShort()
                CLS_INTEGER -> numParam.toInt()
                CLS_LONG -> numParam.toLong()
                CLS_Float -> numParam.toFloat()
                CLS_Double -> numParam.toDouble()
            }
            if (METHOD_VALUE_OF == method.name) {
                replaceValueOfMethod(context, node, clsName)
            }
        } catch (ex: Exception) {
            report(context, node, "Please pass in the correct parameters")
        }
    }

    private fun replaceValueOfMethod(
        context: JavaContext, node: UCallExpression, clsName: String?
    ) {
        val parseMethod = getParseMethod(clsName)
        if (parseMethod.isNotBlank()) {
            report(
                context,
                node,
                "Please replace $METHOD_VALUE_OF to $parseMethod.",
                fix().replace().text(METHOD_VALUE_OF).with(parseMethod).autoFix().build()
            )
        }
    }

    private fun getParseMethod(cls: String?): String {
        return when (cls) {
            CLS_BYTE -> METHOD_PARSE_BYTE
            CLS_SHORT -> METHOD_PARSE_SHORT
            CLS_INTEGER -> METHOD_PARSE_INT
            CLS_LONG -> METHOD_PARSE_LONG
            CLS_Float -> METHOD_PARSE_FLOAT
            CLS_Double -> METHOD_PARSE_DOUBLE
            else -> ""
        }
    }

    private fun report(
        context: JavaContext, node: UCallExpression, message: String, quickfixData: LintFix? = null
    ) {
        context.report(ISSUE, node, context.getLocation(node), message, quickfixData)
    }
}
