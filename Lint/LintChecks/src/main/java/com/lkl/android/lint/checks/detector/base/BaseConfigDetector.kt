package com.lkl.android.lint.checks.detector.base

import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
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
        const val KEY_FIXES = "fixes"
    }

    private var lintConfig: LintConfig? = null
    protected var customConfig: JsonObject? = null

    override fun beforeCheckRootProject(context: Context) {
        // 读取配置
        lintConfig = LintConfig.getInstance(context)

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

    fun getDetectMethodNames(): List<String> {
        return GsonUtils.parseJson2List(
            getJsonStringConfig(KEY_DETECT_METHOD_NAMES), String::class.java
        ) ?: ArrayList()
    }
}
