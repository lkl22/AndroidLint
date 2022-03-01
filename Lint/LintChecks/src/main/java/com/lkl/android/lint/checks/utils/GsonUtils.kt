package com.lkl.android.lint.checks.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

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

    fun getJsonString(jsonObject: JsonObject, key: String): String? {
        return try {
            jsonObject.get(key).toString()
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

    /**
     * json字符串通过Gson框架生成对象
     *
     * @param jsonData 原始json数据
     * @param clazz 要解析成的数据实体类
     * @return 解析后的实体对象
     */
    @JvmStatic
    fun <T> parseJson2Obj(jsonData: String?, clazz: Class<T>): T? {
        if (jsonData.isNullOrBlank()) {
            return null
        }
        return try {
            Gson().fromJson(jsonData, clazz)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * json字符串通过Gson框架生成对象
     *
     * @param jsonData 原始json数据
     * @param clazz 要解析成的数据实体类
     * @return 解析后的实体对象
     */
    @JvmStatic
    fun <T> parseJson2List(jsonData: String?, clazz: Class<T>): ArrayList<T>? {
        if (jsonData.isNullOrBlank()) {
            return null
        }
        return try {
            val type = type(ArrayList::class.java, clazz)
            Gson().fromJson<ArrayList<T>>(jsonData, type)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 将java对象转换成json字符串
     *
     * @param obj java对象
     * @return json字符串
     */
    fun parseObj2Json(obj: Any?): String {
        if (null == obj) {
            return ""
        }
        return try {
            Gson().toJson(obj)
        } catch (e: Exception) {
            ""
        }
    }

    private fun type(raw: Class<*>, vararg args: Type): ParameterizedType {
        return object : ParameterizedType {
            override fun getRawType(): Type {
                return raw
            }

            override fun getActualTypeArguments(): Array<out Type> {
                return args
            }

            override fun getOwnerType(): Type? {
                return null
            }
        }
    }
}
