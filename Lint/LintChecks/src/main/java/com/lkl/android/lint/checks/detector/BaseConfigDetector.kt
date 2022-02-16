package com.lkl.android.lint.checks.detector

import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.LintFix
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.lkl.android.lint.checks.bean.FixItem
import com.lkl.android.lint.checks.config.LintConfig
import com.lkl.android.lint.checks.utils.GsonUtils


/**
 * 读取配置的Detector基类
 *
 * @author lkl
 * @since 2022/02/09
 */
abstract class BaseConfigDetector : Detector() {
    companion object {
        const val KEY_REPORT_MESSAGE = "reportMessage"
        const val KEY_DETECT_METHOD_NAMES = "detectMethodNames"
        const val KEY_IGNORES = "ignores"
        const val KEY_FIX_DISPLAY_NAME = "fixDisplayName"
        const val KEY_FIX_CLASS_NAME = "fixClassName"
        const val KEY_FIXES = "fixes"
    }

    private var lintConfig: LintConfig? = null
    protected var customConfig: JsonObject? = null

    protected var fixes: List<FixItem>? = null

    override fun beforeCheckEachProject(context: Context) {
        // 读取配置
        lintConfig = LintConfig(context)

        customConfig = getUsageConfig()

        fixes = GsonUtils.parseJson2List(getJsonStringConfig(KEY_FIXES), FixItem::class.java)
    }

    abstract fun getUsageConfig(): JsonObject?

    fun getUsageConfig(key: String): JsonObject? {
        return lintConfig?.findConfig(key = key)
    }

    fun getStringConfig(key: String): String? {
        return customConfig?.let {
            GsonUtils.getString(it, key)
        }
    }

    fun getJsonStringConfig(key: String): String? {
        return customConfig?.let {
            GsonUtils.getJsonString(it, key)
        }
    }

    fun getJsonArrayConfig(key: String): JsonArray? {
        return customConfig?.let {
            GsonUtils.getJsonArray(it, key)
        }
    }

    fun getDetectMethodNames(): List<String> {
        return GsonUtils.parseJson2List(getJsonStringConfig(KEY_DETECT_METHOD_NAMES),
                String::class.java) ?: ArrayList()
    }

    protected fun createReplaceMethodFix(
        methodName: String,
        newMethodName: String,
        isStaticMethod: Boolean,
        receiverTxt: String?
    ): LintFix {
        val fixBuilder = fix().replace().pattern("(${methodName}\\s*\\()").shortenNames().autoFix()
        if (isStaticMethod) {
            fixBuilder.with("${newMethodName}(${if (receiverTxt.isNullOrBlank()) "" else "$receiverTxt, "}")
        } else {
            fixBuilder.with("${newMethodName}(")
        }
        return fixBuilder.build()
    }

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
