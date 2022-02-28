package com.lkl.android.lint.checks.utils

import org.jetbrains.uast.UCallExpression

/**
 * 文件操作工具类
 *
 * @author lkl
 * @since 2022/02/26
 */

/**
 * 获取该表达式的标准名称
 * 例：android.content.ContextWrapper.getSharedPreferences
 */
fun UCallExpression.getQualifiedName(): String {
    return resolve()?.containingClass?.qualifiedName + "." + resolve()?.name
}
