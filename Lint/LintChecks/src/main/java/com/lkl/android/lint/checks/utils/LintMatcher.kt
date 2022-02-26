package com.lkl.android.lint.checks.utils

import com.intellij.psi.PsiClass
import com.lkl.android.lint.checks.bean.BaseConfigProperty
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.getContainingUClass
import org.jetbrains.uast.getQualifiedName
import java.util.regex.Pattern

/**
 * lint 名字匹配器
 *
 * @author lkl
 * @since 2022/02/26
 */
object LintMatcher {
    /**
     * 匹配方法
     */
    fun matchMethod(
        baseConfig: BaseConfigProperty, node: UCallExpression
    ): Boolean {
        return match(
            baseConfig.name,
            baseConfig.nameRegex,
            node.getQualifiedName(),
            node.getContainingUClass()?.qualifiedName,
            baseConfig.exclude,
            baseConfig.excludeRegex
        )
    }

    /**
     * 匹配构造方法
     */
    fun matchConstruction(
        baseConfig: BaseConfigProperty, node: UCallExpression
    ): Boolean {
        return match(
            baseConfig.name,
            baseConfig.nameRegex,
            //不要使用node.resolve()获取构造方法，在没定义构造方法使用默认构造的时候返回值为null
            node.classReference.getQualifiedName(),
            node.getContainingUClass()?.qualifiedName,
            baseConfig.exclude,
            baseConfig.excludeRegex
        )
    }

    /**
     * 匹配继承或实现类
     */
    fun matchInheritClass(
        baseConfig: BaseConfigProperty, node: UClass
    ): Boolean {
        node.supers.forEach {
            if (match(
                    baseConfig.name,
                    baseConfig.nameRegex,
                    it.qualifiedName,
                    node.qualifiedName,
                    baseConfig.exclude,
                    baseConfig.excludeRegex
                )
            ) return true
        }
        return false
    }

    /**
     * 匹配文件名
     */
    fun matchFileName(
        baseConfig: BaseConfigProperty, fileName: String
    ) = match(
        baseConfig.name, baseConfig.nameRegex, fileName
    )

    /**
     *  匹配类
     */
    fun matchClass(
        baseConfig: BaseConfigProperty, node: PsiClass
    ): Boolean {
        return match(
            baseConfig.name,
            baseConfig.nameRegex,
            node.qualifiedName,
            node.containingClass?.qualifiedName,
            baseConfig.exclude,
            baseConfig.excludeRegex
        )
    }


    /**
     * @param name 是完全匹配qualifiedName
     * @param nameRegex 是正则匹配，匹配优先级上name > nameRegex
     * @param qualifiedName 要匹配的name
     * @param inClassName 是当前需要匹配的方法所在类
     * @param exclude 是要排除匹配的类（目前以类的粒度去排除）匹配优先级上 exclude > excludeRegex
     * @param excludeRegex 是要排除匹配的类，正则匹配（目前以类的粒度去排除）
     */
    fun match(
        name: String?,
        nameRegex: String?,
        qualifiedName: String?,
        inClassName: String? = null,
        exclude: List<String>? = null,
        excludeRegex: String? = null
    ): Boolean {
        qualifiedName ?: return false
        // 排除
        if (!inClassName.isNullOrBlank()) {
            if (exclude != null && exclude.contains(inClassName)) return false

            if (!excludeRegex.isNullOrBlank() && Pattern.compile(excludeRegex).matcher(inClassName)
                    .find()
            ) {
                return false
            }
        }

        if (!name.isNullOrBlank() && name == qualifiedName) {
            // 优先匹配name
            return true
        }
        if (!nameRegex.isNullOrBlank() && Pattern.compile(nameRegex).matcher(qualifiedName)
                .find()
        ) {
            // 再匹配nameRegex
            return true
        }
        return false
    }
}