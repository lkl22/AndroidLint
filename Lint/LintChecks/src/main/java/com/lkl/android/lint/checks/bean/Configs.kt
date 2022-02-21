package com.lkl.android.lint.checks.bean

data class FixItem(
    var displayName: String?,
    var className: String?,
    var isStaticMethod: Boolean = false,
    var needCallerParam: Boolean = false,
    var methodMap: Map<String, String>?
)

data class ExportedIgnoreItem(var className: String?, var reason: String?)

data class ExportedIgnoreCfg(
    var activities: List<ExportedIgnoreItem>?,
    var services: List<ExportedIgnoreItem>?,
    var receivers: List<ExportedIgnoreItem>?,
    var providers: List<ExportedIgnoreItem>?
)


data class AttrItem(
    var attrName: String?, var attrValue: String?, var buildVariant: String?, var namespace: String?
)

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

data class ParamInfo(var index: Int = -1, var value: String?)

data class MethodParamItem(
    var className: String?,
    var methodName: String?,
    var buildVariant: String?,
    var reportMessage: String?,
    var params: List<ParamInfo>?
)

data class MethodParamConfig(var reportMessage: String?, var methods: List<MethodParamItem>?)