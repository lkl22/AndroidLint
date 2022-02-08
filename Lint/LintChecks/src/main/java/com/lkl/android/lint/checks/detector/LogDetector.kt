/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lkl.android.lint.checks.detector

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression


/**
 * Sample detector showing how to analyze Kotlin/Java code. This example
 * flags all string literals in the code that contain the word "lint".
 */
@Suppress("UnstableApiUsage")
class LogDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String> {
        return listOf("v", "d", "i", "w", "e", "wtf")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (context.evaluator.isMemberInClass(method, "android.util.Log")) {
            context.report(ISSUE, node, context.getLocation(node), "Do not directly invoke android.util.Log methods.", getLintFix())
        }
    }

    /**
     * lint自动修复
     */
    private fun getLintFix(): LintFix {
        return fix().replace()
            .name("Replace Log() with LogUtils()")
            .text("Log.")
            .with("com.lkl.androidlint.utils.LogUtils.")
            .shortenNames()
            .autoFix()
            .build()
    }

    companion object {
        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "LogUsage",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Do not directly invoke android.util.Log methods.",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = "Do not directly invoke android.util.Log methods, should use the unified tool class", // no need to .trimIndent(), lint does that automatically
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                LogDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
