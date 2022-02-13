package com.lkl.android.lint.checks.utils

import org.jetbrains.uast.UExpression

/**
 * Detector工具类
 *
 * @author lkl
 * @since 2022/02/13
 */
object DetectorUtils {
    /**
     * 获取调用方法前面的Receiver文本
     *     intent.getBooleanExtra() -> intent
     *     getIntent().getBooleanExtra() -> getIntent()
     * @param receiver receiver UExpression
     * @return Receiver文本
     */
    fun getReceiverTxt(receiver: UExpression?): String {
//        return when (receiver) {
//            is JavaUCallExpression, is KotlinUFunctionCallExpression -> receiver.tryResolveNamed()?.name ?: ""
//            is JavaUSimpleNameReferenceExpression -> receiver.tryResolveNamed()?.name ?: ""
//            is KotlinUSimpleReferenceExpression -> receiver.identifier
//            else -> ""
//        }
        return receiver?.asSourceString() ?: ""
    }
}