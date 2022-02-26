package com.lkl.android.lint.checks.detector

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import com.android.xml.AndroidManifest
import com.google.gson.JsonObject
import com.lkl.android.lint.checks.bean.AttrItem
import com.lkl.android.lint.checks.utils.DetectorUtils
import com.lkl.android.lint.checks.utils.GsonUtils
import org.w3c.dom.Element


/**
 * 检查Application Attr相关配置
 *
 * @author lkl
 * @since 2022/02/21
 */
@Suppress("UnstableApiUsage")
class ApplicationAttrDetector : BaseConfigDetector(), XmlScanner {
    companion object {
        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "ApplicationAttribute",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "some attribute better to set correct value.",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = "some attribute better to set correct value.", // no need to .trimIndent(), lint does that automatically
            category = Category.SECURITY,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                ApplicationAttrDetector::class.java, Scope.MANIFEST_SCOPE
            )
        )
    }

    private var attrs: List<AttrItem>? = null

    override fun beforeCheckEachProject(context: Context) {
        super.beforeCheckEachProject(context)
        attrs = GsonUtils.parseJson2List(getJsonStringConfig("attrs"), AttrItem::class.java)
    }

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("application-attr")
    }

    override fun getApplicableElements(): Collection<String> {
        return listOf(AndroidManifest.NODE_APPLICATION)
    }

    override fun visitElement(context: XmlContext, element: Element) {
        attrs?.forEach {
            if (!DetectorUtils.isBuildVariant(context, it.buildVariant)) {
                return@forEach
            }

            if (!it.attrName.isNullOrBlank() && !it.attrValue.isNullOrBlank()) {
                val ns = it.namespace ?: SdkConstants.ANDROID_URI
                val attr = element.getAttributeNodeNS(ns, it.attrName)
                if (attr == null || attr.value != it.attrValue) {
                    val scope = attr ?: element
                    context.report(
                        ISSUE,
                        scope,
                        context.getLocation(scope),
                        "${it.attrName} attribute better to set to ${it.attrValue}.",
                        fix().set(
                            ns, it.attrName, it.attrValue
                        ).build()
                    )
                }
            }
        }
    }
}
