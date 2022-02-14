package com.lkl.android.lint.checks.detector

import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.google.gson.JsonArray
import com.google.gson.JsonObject
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
        const val KEY_FIX_IS_STATIC_METHOD = "fixIsStaticMethod"
        const val KEY_FIX_METHOD_MAP = "fixMethodMap"
    }

    private var lintConfig: LintConfig? = null
    protected var customConfig: JsonObject? = null

    override fun beforeCheckEachProject(context: Context) {
        // 读取配置
        lintConfig = LintConfig(context)

        customConfig = getUsageConfig()
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
        val list = ArrayList<String>()
        getJsonArrayConfig(KEY_DETECT_METHOD_NAMES)?.mapTo(list, {
            try {
                it.asString
            } catch (ex: Exception) {
                ""
            }
        })
        return list.filter { it.isNotBlank() }
    }
}
