package com.lkl.android.lint.checks.detector.xml

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import com.android.xml.AndroidManifest
import com.google.gson.JsonObject
import com.lkl.android.lint.checks.bean.ExportedIgnoreCfg
import com.lkl.android.lint.checks.bean.ExportedIgnoreItem
import com.lkl.android.lint.checks.detector.base.BaseConfigDetector
import com.lkl.android.lint.checks.utils.GsonUtils
import org.w3c.dom.Element


/**
 * 检查四大组件的exported属性
 *
 * @author lkl
 * @since 2022/02/15
 */
@Suppress("UnstableApiUsage")
class ExportedAttrDetector : BaseConfigDetector(), XmlScanner {
    companion object {
        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "ExportedAttribute",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Exported attribute better to set to false.",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = "Exported attribute better to set to false, set to true must have reason.", // no need to .trimIndent(), lint does that automatically
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                ExportedAttrDetector::class.java,
                Scope.MANIFEST_SCOPE
            )
        )
    }

    private var ignoreCfg: ExportedIgnoreCfg? = null

    override fun beforeCheckRootProject(context: Context) {
        super.beforeCheckRootProject(context)
        ignoreCfg =
            GsonUtils.parseJson2Obj(getJsonStringConfig(KEY_IGNORES), ExportedIgnoreCfg::class.java)
    }

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("exported-attr")
    }

    override fun getApplicableElements(): Collection<String> {
        return listOf(
            AndroidManifest.NODE_ACTIVITY,
            AndroidManifest.NODE_SERVICE,
            AndroidManifest.NODE_RECEIVER,
            AndroidManifest.NODE_PROVIDER
        )
    }

    override fun visitElement(context: XmlContext, element: Element) {
        val attrExported =
            element.getAttributeNodeNS(SdkConstants.ANDROID_URI, SdkConstants.ATTR_EXPORTED)

        if (attrExported == null || attrExported.value != SdkConstants.VALUE_FALSE) {
            getExportedIgnoreItems(element.tagName)?.apply {
                val packName =
                    context.document.documentElement.getAttribute(SdkConstants.ATTR_PACKAGE)
                val attrName =
                    element.getAttributeNS(SdkConstants.ANDROID_URI, SdkConstants.ATTR_NAME)
                val className = if (attrName.startsWith(".")) {
                    packName + attrName
                } else {
                    attrName
                }
                if (any {
                        it.className == className && !it.reason.isNullOrBlank()
                    }) {
                    return@visitElement
                }
            }
            val scope = attrExported ?: element
            context.report(
                ISSUE,
                scope,
                context.getLocation(scope),
                "Exported attribute better to set to false.",
                fix().set(
                    SdkConstants.ANDROID_URI,
                    SdkConstants.ATTR_EXPORTED,
                    SdkConstants.VALUE_FALSE
                ).build()
            )
        }
    }

    private fun getExportedIgnoreItems(tagName: String): List<ExportedIgnoreItem>? {
        return when (tagName) {
            AndroidManifest.NODE_ACTIVITY -> ignoreCfg?.activities
            AndroidManifest.NODE_SERVICE -> ignoreCfg?.services
            AndroidManifest.NODE_RECEIVER -> ignoreCfg?.receivers
            AndroidManifest.NODE_PROVIDER -> ignoreCfg?.providers
            else -> null
        }
    }
}
