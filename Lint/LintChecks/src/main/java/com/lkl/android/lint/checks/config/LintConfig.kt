package com.lkl.android.lint.checks.config

import com.android.tools.lint.detector.api.Context
import com.google.gson.JsonObject
import com.lkl.android.lint.checks.utils.FileUtils
import com.lkl.android.lint.checks.utils.GsonUtils
import java.io.File

/**
 * lint config管理类
 *
 * @author lkl
 * @since 2022/02/09
 */
class LintConfig private constructor(context: Context) {
    companion object {
        private const val CONFIG_DIR_NAME = "lintConfig"
        const val CONFIG_FILE_NAME = "custom-lint-config.json"
        private const val SETTINGS_GRADLE_FILE_NAME = "settings.gradle"

        @Volatile
        private var instance: LintConfig? = null
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: LintConfig(context).also { instance = it }
        }
    }

    private val configJsonObject: JsonObject?

    init {
        configJsonObject = findConfigFile(context.project.dir)?.let {
            GsonUtils.parseJsonObject(it)
        }
    }

    fun findConfig(key: String): JsonObject? {
        return configJsonObject?.let {
            GsonUtils.getJsonObject(it, key)
        }
    }

    /**
     * find custom lint rules config file
     *
     * @param dir 查找的目录
     * @return config file or null
     */
    private fun findConfigFile(dir: File): File? {
        return findRootProjectPath(dir)?.let {
            if (FileUtils.isExistDir(it, CONFIG_DIR_NAME)) {
                val configDir = File(it, CONFIG_DIR_NAME)
                if (FileUtils.isExistFile(configDir, CONFIG_FILE_NAME)) {
                    return File(configDir, CONFIG_FILE_NAME)
                }
            }
            null
        }
    }

    private fun findRootProjectPath(dir: File): File? {
        if (!dir.exists() || !dir.isDirectory) {
            return null
        }
        return if (isRootDir(dir)) dir else findRootProjectPath(dir.parentFile)
    }

    /**
     * 是否项目根目录
     *
     * @param dir 查找的目录
     * @return true 项目根目录
     */
    private fun isRootDir(dir: File): Boolean {
        return FileUtils.isExistFile(dir, SETTINGS_GRADLE_FILE_NAME)
    }
}