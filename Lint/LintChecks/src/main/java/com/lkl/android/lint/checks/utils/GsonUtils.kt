package com.lkl.android.lint.checks.utils

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

/**
 * json解析工具类
 *
 * @author lkl
 * @since 2022/02/09
 */
object GsonUtils {
    fun parseJsonObject(file: File): JsonObject? {
        return try {
            JsonParser().parse(file.readText()).asJsonObject
        } catch (ex: Exception) {
            null
        }
    }

    fun getJsonObject(file: File, key: String): JsonObject? {
        val jsonObject = parseJsonObject(file) ?: return null
        return getJsonObject(jsonObject, key)
    }

    fun getJsonObject(jsonObject: JsonObject, key: String): JsonObject? {
        return try {
            jsonObject.get(key).asJsonObject
        } catch (ex: Exception) {
            null
        }
    }

    fun getString(jsonObject: JsonObject, key: String): String? {
        return try {
            jsonObject.get(key).asString
        } catch (ex: Exception) {
            null
        }
    }

    fun getJsonArray(jsonObject: JsonObject, key: String): JsonArray? {
        return try {
            jsonObject.get(key).asJsonArray
        } catch (ex: Exception) {
            null
        }
    }
}
