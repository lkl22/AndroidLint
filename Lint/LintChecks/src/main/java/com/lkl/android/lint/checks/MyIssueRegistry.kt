/*
 * Copyright (C) 2021 The Android Open Source Project
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
package com.lkl.android.lint.checks

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.lkl.android.lint.checks.detector.source.*
import com.lkl.android.lint.checks.detector.xml.ApplicationAttrDetector
import com.lkl.android.lint.checks.detector.xml.ExportedAttrDetector
import com.lkl.android.lint.checks.detector.xml.ResourceNameDetector
import com.lkl.android.lint.checks.detector.xml.UsesPermissionDetector

/*
 * The list of issues that will be checked when running <code>lint</code>.
 */
@Suppress("UnstableApiUsage")
class MyIssueRegistry : IssueRegistry() {
    override val issues = listOf(
        ApiDetector.ISSUE,
        BundleDetector.ISSUE,
        DisableConstructorDetector.ISSUE,
        InheritDetector.ISSUE,
        IntentDetector.ISSUE,
        LogDetector.ISSUE,
        MethodParamDetector.ISSUE,
        NumParseDetector.ISSUE,
        SerializableClassDetector.ISSUE,
        StartActivityDetector.ISSUE,

        ApplicationAttrDetector.ISSUE,
        ExportedAttrDetector.ISSUE,
        ResourceNameDetector.ISSUE,
        UsesPermissionDetector.ISSUE
    )

    override val api: Int
        get() = CURRENT_API

    override val minApi: Int
        get() = 5 // works with Studio 4.1 or later; see com.android.tools.lint.detector.api.Api / ApiKt

    // Requires lint API 30.0+; if you're still building for something
    // older, just remove this property.
//    override val vendor: Vendor = Vendor(
//        vendorName = "Android Open Source Project",
//        feedbackUrl = "https://github.com/googlesamples/android-custom-lint-rules/issues",
//        contact = "https://github.com/googlesamples/android-custom-lint-rules"
//    )
}
