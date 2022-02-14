package com.lkl.android.lint.checks.bean

data class ExportedIgnoreItem(var className: String?, var reason: String?)

data class ExportedIgnoreCfg(
    var activities: List<ExportedIgnoreItem>?,
    var services: List<ExportedIgnoreItem>?,
    var receivers: List<ExportedIgnoreItem>?,
    var providers: List<ExportedIgnoreItem>?
)