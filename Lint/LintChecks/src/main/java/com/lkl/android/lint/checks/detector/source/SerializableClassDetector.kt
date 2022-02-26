package com.lkl.android.lint.checks.detector.source

import com.android.tools.lint.detector.api.*
import com.google.gson.JsonObject
import com.intellij.psi.PsiClassType
import com.lkl.android.lint.checks.bean.BaseConfigProperty
import com.lkl.android.lint.checks.detector.base.BaseSourceCodeDetector
import com.lkl.android.lint.checks.utils.GsonUtils
import com.lkl.android.lint.checks.utils.LintMatcher
import org.jetbrains.uast.UClass
import com.lkl.android.lint.checks.utils.report

/**
 * 实现了Serializable接口的类，引用类型成员变量也必须要实现Serializable接口
 *
 * @author lkl
 * @since 2022/02/20
 */
class SerializableClassDetector : BaseSourceCodeDetector() {

    companion object {
        private const val REPORT_MESSAGE = "该对象必须要实现Serializable接口，因为外部类实现了Serializable接口"
        private const val CLASS_SERIALIZABLE = "java.io.Serializable"

        val ISSUE = Issue.create(
            "SerializableClassCheck",
            REPORT_MESSAGE,
            REPORT_MESSAGE,
            Category.CORRECTNESS,
            10,
            Severity.ERROR,
            Implementation(SerializableClassDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    private var lintConfig: BaseConfigProperty? = null

    override fun getUsageConfig(): JsonObject? {
        return getUsageConfig("serializable_usage")
    }

    override fun beforeCheckRootProject(context: Context) {
        super.beforeCheckRootProject(context)

        lintConfig =
            GsonUtils.parseJson2Obj(customConfig?.toString(), BaseConfigProperty::class.java)
    }

    override fun applicableSuperClasses(): List<String>? {
        return listOf(CLASS_SERIALIZABLE)
    }

    override fun visitClass(context: JavaContext, declaration: UClass) {
        val configProperty = lintConfig ?: return
        for (field in declaration.fields) {
            // 字段是引用类型，并且可以拿到该class
            val psiClass = (field.type as? PsiClassType)?.resolve() ?: continue
            if (!LintMatcher.matchClass(
                    configProperty, psiClass
                )
            ) {
                return
            }
            val typeReference = field.typeReference ?: return
            if (!context.evaluator.implementsInterface(psiClass, CLASS_SERIALIZABLE, true)) {
                context.report(
                    ISSUE,
                    typeReference,
                    context.getLocation(typeReference),
                    configProperty.reportMessage,
                    configProperty.lintSeverity,
                    null
                )
            }
        }
    }

}