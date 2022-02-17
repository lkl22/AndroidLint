package com.lkl.android.lint.checks.detector

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.intellij.psi.PsiMethod
import com.lkl.android.lint.checks.bean.FixItem
import com.lkl.android.lint.checks.config.LintConfig
import com.lkl.android.lint.checks.utils.DetectorUtils
import com.lkl.android.lint.checks.utils.GsonUtils
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.uast.UCallExpression


/**
 * SourceCodeScanner的Detector基类
 *
 * @author lkl
 * @since 2022/02/09
 */
abstract class BaseSourceCodeDetector : BaseConfigDetector(), SourceCodeScanner {

    protected var fixes: List<FixItem>? = null

    override fun beforeCheckEachProject(context: Context) {
        super.beforeCheckEachProject(context)

        fixes = GsonUtils.parseJson2List(getJsonStringConfig(KEY_FIXES), FixItem::class.java)
    }

    protected fun getFix(
        context: JavaContext, node: UCallExpression, method: PsiMethod
    ): LintFix {
        // isKotlin(method)不准
        val isKotlinCode = context.psiFile is KtFile
        val receiverTxt = DetectorUtils.getReceiverTxt(node.receiver)

        val builder = fix().alternatives()

        fixes?.forEach {
            val compositeBuilder = fix().name(it.displayName).composite()

            val newMethodName = getNewMethodName(it.methodMap, method.name)
            val firstParam = getFirstParam(it.isStaticMethod, receiverTxt)
            // 替换方法名必须在替换Receiver之前
            if (method.name != newMethodName || !firstParam.isNullOrBlank()) {
                // 方法名需要修改或者需要添加第一个参数才需要该fix
                compositeBuilder.add(
                    createReplaceMethodFix(
                        method.name, newMethodName, firstParam
                    )
                )
            }
            it.className?.apply {
                compositeBuilder.add(
                    createReplaceReceiverFix(
                        this, isKotlinCode, it.isStaticMethod, receiverTxt
                    )
                )
            }

            builder.add(compositeBuilder.build())
        }
        return builder.build()
    }

    /**
     * 获取要替换的方法名
     *
     * @param methodMap 方法名映射表
     * @param methodName 原有方法名
     * @return 新的方法名
     */
    open protected fun getNewMethodName(methodMap: Map<String, String>?, methodName: String): String {
        return methodMap?.get(methodName) ?: methodName
    }

    /**
     * 获取要插入第一个位置的参数，默认不需要
     *
     * @param isStaticMethod true 要替换的方法是静态方法
     * @param receiverTxt 原有方法的调用方
     * @return 要插入第一个位置的参数
     */
    open protected fun getFirstParam(isStaticMethod: Boolean, receiverTxt: String): String? {
        return null
    }

    /**
     * 创建自动fix方法调用方的fix对象
     *
     * @param methodName 原有的方法名
     * @param newMethodName 新的方法名
     * @param firstParam 方法第一个参数
     * @return LintFix对象
     */
    protected fun createReplaceMethodFix(
        methodName: String, newMethodName: String, firstParam: String?
    ): LintFix {
        val fixBuilder = fix().replace().pattern("(${methodName}\\s*\\()").shortenNames().autoFix()
        fixBuilder.with("${newMethodName}(${if (firstParam.isNullOrBlank()) "" else "$firstParam, "}")
        return fixBuilder.build()
    }

    /**
     * 创建自动fix方法调用体的fix对象
     *
     * @param fixClassName 要使用的class全类名
     * @param isKotlinCode true kotlin语言
     * @param isStaticMethod true 要替换的方法是静态方法
     * @param receiverTxt 要替换的文本
     * @return LintFix对象
     */
    protected fun createReplaceReceiverFix(
        fixClassName: String, isKotlinCode: Boolean, isStaticMethod: Boolean, receiverTxt: String
    ): LintFix {
        val fixBuilder = fix().replace().text(receiverTxt).shortenNames().autoFix()
        if (isStaticMethod) {
            fixBuilder.with(fixClassName)
        } else {
            fixBuilder.with("${if (isKotlinCode) "" else "new "}${fixClassName}($receiverTxt)")
        }
        return fixBuilder.build()
    }
}