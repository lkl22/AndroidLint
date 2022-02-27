package com.lkl.android.lint.checks.bean

import com.android.tools.lint.detector.api.Severity

/**
 * lint配置基础属性
 *
 * @param reportMessage report message
 * @param buildVariant 当前执行task的buildVariant，传了，只有指定的task会校验
 * @param severity report Severity级别
 */
open class BaseConfigProperty(
    val reportMessage: String? = null,
    var buildVariant: String? = null,
    private val severity: String? = "error"
) {
    val lintSeverity
        get() = when (severity) {
            "fatal" -> Severity.FATAL
            "error" -> Severity.ERROR
            "warning" -> Severity.WARNING
            "informational" -> Severity.INFORMATIONAL
            "ignore" -> Severity.IGNORE
            else -> Severity.ERROR
        }
}

/**
 * lint Match配置基础属性
 */
open class BaseMatchConfigProperty(
    val name: String = "",
    val nameRegex: String = "",
    val exclude: List<String> = listOf(),
    val excludeRegex: String = ""
) : BaseConfigProperty()

/**
 * 资源命名规范
 *
 * @param drawable 图片资源
 * @param layout layout布局资源
 */
data class ResourceNameRule(
    val drawable: BaseMatchConfigProperty = BaseMatchConfigProperty(),
    val layout: BaseMatchConfigProperty = BaseMatchConfigProperty()
)

/**
 * 自动修复参数配置
 *
 * @param displayName fix的显示名称
 * @param className 需要替换receive的类名
 * @param isStaticMethod 替换的方法是否是静态方法，true 静态方法
 * @param needCallerParam 是否需要将receive作为替换方法的第一个参数，true 需要
 * @param methodMap 方法名的转换映射，可以将方法名换为新的指定方法
 */
data class FixItem(
    var displayName: String?,
    var className: String?,
    var isStaticMethod: Boolean = false,
    var needCallerParam: Boolean = false,
    var methodMap: Map<String, String>?
)

/**
 * 四大组件的exported属性需要设置为true的理由配置，忽略ExportedAttribute告警
 *
 * @param className 四大组件的全类名
 * @param reason ignore的理由
 */
data class ExportedIgnoreItem(var className: String?, var reason: String?)

/**
 * 四大组件的exported属性需要设置为true的理由配置，忽略ExportedAttribute告警
 *
 * @param activities activity组件
 * @param services service组件
 * @param receivers receiver组件
 * @param providers provider组件
 */
data class ExportedIgnoreCfg(
    var activities: List<ExportedIgnoreItem>?,
    var services: List<ExportedIgnoreItem>?,
    var receivers: List<ExportedIgnoreItem>?,
    var providers: List<ExportedIgnoreItem>?
)

/**
 * 检查application tag的attr属性配置是否正确配置
 *
 * @param attrName attr属性名
 * @param attrValue attr属性值
 * @param buildVariant 当前执行task的buildVariant
 * @param namespace 属性所属的命名空间
 */
data class AttrItem(
    var attrName: String?, var attrValue: String?, var buildVariant: String?, var namespace: String?
)

/**
 * 使用了属于最小权限申请权限，配置理由忽略告警
 *
 * @param permissionName 权限名
 * @param reason 使用理由
 */
data class PermissionIgnoreItem(var permissionName: String?, var reason: String?)

data class ApiItem(
    var className: String?,
    var buildVariant: String?,
    var reportMessage: String?,
    var methodNames: List<String>?,
    var fixes: List<FixItem>?
)

data class ApiUsage(
    var reportMessage: String?,
    var deprecatedMethod: List<ApiItem>?,
    var handleException: List<ApiItem>?
)

/**
 * 方法的参数信息
 *
 * @param index 参数的位置，从0开始
 * @param value 参数值
 */
data class ParamInfo(var index: Int = -1, var value: String?)

/**
 * 需要检查方法参数的配置项
 *
 * @param className 方法所属的类名
 * @param methodName 方法名
 * @param buildVariant 当前执行task的buildVariant，传了，只有指定的task会校验该方法参数
 * @param reportMessage report时显示的message，优先级高于MethodParamConfig里的配置
 * @param params 要校验的参数列表
 */
data class MethodParamItem(
    var className: String?,
    var methodName: String?,
    var buildVariant: String?,
    var reportMessage: String?,
    var params: List<ParamInfo>?
)

/**
 * 需要检查方法参数的配置
 *
 * @param reportMessage report时显示的message
 * @param methods 要检查的方法列表
 */
data class MethodParamConfig(var reportMessage: String?, var methods: List<MethodParamItem>?)

/**
 * 需要校验的构造方法配置
 *
 * @param constructorName 构造方法名
 */
data class ConstructorItem(val constructorName: String?) : BaseConfigProperty()

/**
 * 需要校验的构造方法配置项
 *
 * @param reportMessage 默认的report message
 * @param constructors 需要校验的构造方法配置列表
 */
data class ConstructorUsage(val reportMessage: String?, val constructors: List<ConstructorItem>?)

/**
 * 需要校验的继承配置
 *
 * @param className 继承的类名/接口名
 */
data class InheritItem(val className: String?) : BaseConfigProperty()

/**
 * 需要校验的继承配置项
 *
 * @param reportMessage 默认的report message
 * @param inherits 需要校验的继承配置列表
 */
data class InheritUsage(val reportMessage: String?, val inherits: List<InheritItem>?)