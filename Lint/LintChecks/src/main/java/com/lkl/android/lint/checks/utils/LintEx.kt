package com.lkl.android.lint.checks.utils

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.w3c.dom.Node

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

fun JavaContext.report(
    issue: Issue,
    scope: UElement?,
    location: Location,
    message: String,
    severity: Severity,
    quickfixData: LintFix? = null
) {
    this.report(getNewIssue(issue, message, severity), scope, location, message, quickfixData)
}

fun XmlContext.report(
    issue: Issue,
    scope: Node?,
    location: Location,
    message: String,
    severity: Severity,
    quickfixData: LintFix? = null
) {
    this.report(getNewIssue(issue, message, severity), scope, location, message, quickfixData)
}

fun getNewIssue(issue: Issue, message: String, severity: Severity): Issue {
    return Issue.create(
        issue.id,
        message,
        issue.getExplanation(TextFormat.TEXT),
        issue.category,
        issue.priority,
        severity,
        issue.implementation
    )
}
