package com.lkl.android.lint.checks.detector

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.intellij.psi.PsiMethod
import com.lkl.android.lint.checks.utils.DetectorUtils
import com.lkl.android.lint.checks.utils.GsonUtils
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.uast.UCallExpression


/**
 * 检查Intent里部分方法的使用
 *
 * @author lkl
 * @since 2022/02/15
 */
@Suppress("UnstableApiUsage")
class IntentDetector : BaseConfigDetector(), SourceCodeScanner {

    companion object {
        const val INTENT_CLS = "android.content.Intent"

        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "IntentUsage",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Do not directly invoke $INTENT_CLS some methods.",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = "Do not directly invoke $INTENT_CLS some methods, should use the unified tool class", // no need to .trimIndent(), lint does that automatically
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                IntentDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("intent-usage")
    }

    override fun getApplicableMethodNames(): List<String> {
        return getDetectMethodNames()
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (context.evaluator.isMemberInClass(method, INTENT_CLS)) {
            if (customConfig == null) {
                return
            }
            val reportMessage =
                getStringConfig(KEY_REPORT_MESSAGE)
                    ?: "Do not directly invoke $INTENT_CLS some methods."

            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                reportMessage,
                getFix(context, node, method)
            )
        }
    }

    private fun getFix(context: JavaContext, node: UCallExpression, method: PsiMethod): LintFix {
        // isKotlin(method)不准
        val isKotlinCode = context.psiFile is KtFile
        val receiverTxt = DetectorUtils.getReceiverTxt(node.receiver)

        val groupFixDisplayName = getStringConfig(KEY_FIX_DISPLAY_NAME)
        val builder = fix().name(groupFixDisplayName).alternatives()
        val fixes: JsonArray? = getJsonArrayConfig(KEY_FIXES)
        fixes?.forEach {
            if (it is JsonObject) {
                val fixDisplayName = GsonUtils.getString(it, KEY_FIX_DISPLAY_NAME)
                val fixClassName = GsonUtils.getString(it, KEY_FIX_CLASS_NAME)
                val isStaticMethod =
                    GsonUtils.getString(it, KEY_FIX_IS_STATIC_METHOD).toBoolean()
                val compositeBuilder = fix().name(fixDisplayName).composite()
                val methodMap = GsonUtils.getJsonObject(it, KEY_FIX_METHOD_MAP)

                // 替换方法名必须在替换Receiver之前
                compositeBuilder.add(
                    createReplaceMethodFix(
                        method.name,
                        isStaticMethod,
                        methodMap,
                        receiverTxt
                    )
                )
                fixClassName?.apply {
                    compositeBuilder.add(
                        createReplaceReceiverFix(
                            this,
                            isKotlinCode,
                            isStaticMethod,
                            receiverTxt
                        )
                    )
                }

                builder.add(compositeBuilder.build())
            }
        }
        return builder.build()
    }

    private fun createReplaceMethodFix(
        methodName: String,
        isStaticMethod: Boolean,
        methodMap: JsonObject?,
        receiverTxt: String
    ): LintFix {
        val fixBuilder = fix().replace()
            .pattern("(${methodName}\\s*\\()")
            .shortenNames()
            .autoFix()
        val newMethodName = methodMap?.let {
            GsonUtils.getString(it, methodName)
        } ?: methodName
        if (isStaticMethod) {
            fixBuilder.with("${newMethodName}(${receiverTxt}, ")
        } else {
            fixBuilder.with("${newMethodName}(")
        }
        return fixBuilder.build()
    }


    private fun createReplaceReceiverFix(
        fixClassName: String,
        isKotlinCode: Boolean,
        isStaticMethod: Boolean,
        receiverTxt: String
    ): LintFix {
        val fixBuilder = fix().replace()
            .text(receiverTxt)
            .shortenNames()
            .autoFix()
        if (isStaticMethod) {
            fixBuilder.with(fixClassName)
        } else {
            fixBuilder.with("${if (isKotlinCode) "" else "new "}${fixClassName}($receiverTxt)")
        }
        return fixBuilder.build()
    }
}
