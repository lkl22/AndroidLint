package com.lkl.android.lint.checks.detector.xml

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import com.android.xml.AndroidManifest
import com.google.gson.JsonObject
import com.lkl.android.lint.checks.bean.PermissionIgnoreItem
import com.lkl.android.lint.checks.detector.base.BaseConfigDetector
import com.lkl.android.lint.checks.utils.GsonUtils
import org.w3c.dom.Element


/**
 * 最小权限声明，一些权限的使用需要明确的使用场景
 *
 * @author lkl
 * @since 2022/02/15
 */
@Suppress("UnstableApiUsage")
class UsesPermissionDetector : BaseConfigDetector(), XmlScanner {
    companion object {
        private const val REPORT_MESSAGE =
            "Uses-Permission declaration must have usage scenarios. Please remove if you don't use the scene."

        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "MinUsesPermissionDeclaration",
            briefDescription = REPORT_MESSAGE,
            explanation = REPORT_MESSAGE,
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                UsesPermissionDetector::class.java, Scope.MANIFEST_SCOPE
            )
        )
    }

    private var permissionList: List<String>? = null
    private var ignoreCfg: List<PermissionIgnoreItem>? = null

    override fun beforeCheckRootProject(context: Context) {
        super.beforeCheckRootProject(context)
        permissionList = GsonUtils.parseJson2List(
            getJsonStringConfig("permissionList"), String::class.java
        )
        ignoreCfg = GsonUtils.parseJson2List(
            getJsonStringConfig(KEY_IGNORES), PermissionIgnoreItem::class.java
        )
    }

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("uses-permission-declaration")
    }

    override fun getApplicableElements(): Collection<String> {
        return listOf(AndroidManifest.NODE_USES_PERMISSION)
    }

    override fun visitElement(context: XmlContext, element: Element) {
        val attrName = element.getAttributeNS(SdkConstants.ANDROID_URI, SdkConstants.ATTR_NAME)

        permissionList?.apply {
            if (contains(attrName)) {
                if (ignoreCfg != null && ignoreCfg!!.any {
                        attrName == it.permissionName && !it.reason.isNullOrBlank()
                    }) {
                    return@visitElement
                }

                context.report(
                    ISSUE,
                    element,
                    context.getLocation(element),
                    REPORT_MESSAGE,
                    fix().replace().name("Remove uses-permission $attrName").all().with("")
                        .autoFix().build()
                )
                return@visitElement
            }
        }
    }
}
