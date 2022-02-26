package com.lkl.android.lint.checks.config

import com.android.tools.lint.detector.api.*
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
class LintConfig(private val context: Context) {
    companion object {
        private const val CONFIG_DIR_NAME = "lintConfig"
        const val CONFIG_FILE_NAME = "custom-lint-config.json"
        private const val SETTINGS_GRADLE_FILE_NAME = "settings.gradle"
    }

    private val projectDir: File
        get() = context.project.dir

    fun findConfig(dir: File = projectDir, key: String): JsonObject? {
        val configFile = findConfigFile(dir)
        if (configFile != null) {
            val jsonObject = GsonUtils.getJsonObject(configFile, key)
            if (jsonObject != null) {
                return jsonObject
            }
        }
        return if (isRootDir(dir)) {
            // 直到项目根目录都没找到返回null
            null
        } else {
            // 当前目录没有找到配置文件，从父目录重新查找
            findConfig(dir.parentFile, key)
        }
    }

    /**
     * find custom lint rules config file
     *
     * @param dir 查找的目录
     * @return config file or null
     */
    private fun findConfigFile(dir: File): File? {
        if (dir.exists() && dir.isDirectory && FileUtils.isExistDir(dir, CONFIG_DIR_NAME)) {
            val configDir = File(dir, CONFIG_DIR_NAME)
            if (FileUtils.isExistFile(configDir, CONFIG_FILE_NAME)) {
                return File(configDir, CONFIG_FILE_NAME)
            }
        }
        return null
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