package com.lkl.android.lint.checks.detector

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import com.android.xml.AndroidManifest
import com.google.gson.JsonObject
import com.lkl.android.lint.checks.bean.PermissionIgnoreItem
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
        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "Min Uses-Permission declaration",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Uses-Permission declaration must have usage scenarios.",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = "Uses-Permission declaration must have usage scenarios. Please remove if you don't use the scene",
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                UsesPermissionDetector::class.java,
                Scope.MANIFEST_SCOPE
            )
        )
    }

    private var permissionList: List<String>? = null
    private var ignoreCfg: List<PermissionIgnoreItem>? = null

    override fun beforeCheckEachProject(context: Context) {
        super.beforeCheckEachProject(context)
        permissionList = GsonUtils.parseJson2List(
            getJsonStringConfig("permissionList"),
            String::class.java
        )
        ignoreCfg = GsonUtils.parseJson2List(
            getJsonStringConfig(KEY_IGNORES),
            PermissionIgnoreItem::class.java
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
                    "Uses-Permission declaration must have usage scenarios.",
                    fix().replace().name("Remove uses-permission").all().with("").autoFix().build()
                )
                return@visitElement
            }
        }
    }
}
