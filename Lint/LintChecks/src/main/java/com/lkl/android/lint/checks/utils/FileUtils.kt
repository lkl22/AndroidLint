package com.lkl.android.lint.checks.utils

import java.io.File

/**
 * 文件操作工具类
 *
 * @author lkl
 * @since 2022/02/09
 */
object FileUtils {
    /**
     * 查找指定目录下是否存在指定文件
     *
     * @param parentFile 要查找的目录
     * @param fileName 文件名
     * @return true 存在
     */
    fun isExistFile(parentFile: File, fileName: String):Boolean{
        val files = parentFile.listFiles { dir, name ->
            fileName == name && File(dir, name).isFile
        }
        return !files.isNullOrEmpty()
    }

    /**
     * 查找指定目录下是否存在指定文件夹
     *
     * @param parentFile 要查找的目录
     * @param dirName 文件夹名
     * @return true 存在
     */
    fun isExistDir(parentFile: File, dirName: String):Boolean{
        val dirs = parentFile.listFiles { dir, name ->
            dirName == name && File(dir, name).isDirectory
        }
        return !dirs.isNullOrEmpty()
    }
}