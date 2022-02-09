package com.lkl.android.lint.checks.detector

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
        const val KEY_FIX_DISPLAY_NAME = "fixDisplayName"
        const val KEY_FIX_CLASS_NAME = "fixClassName"
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

    fun getConfig(key: String): String? {
        return customConfig?.let {
            GsonUtils.getString(it, key)
        }
    }
}
