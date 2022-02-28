package com.lkl.android.lint.plugin.utils

import java.io.Closeable
import java.io.IOException

/**
 * IO操作工具类
 *
 * @author lkl
 * @since 2022/02/28
 */
object IOUtils {
    fun closeQuietly(vararg closeables: Closeable?) {
        try {
            for (closeable in closeables) {
                closeable?.close()
            }
        } catch (ex: IOException) {
        }
    }
}