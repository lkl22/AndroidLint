package com.lkl.lint.demo.serializable

import java.io.Serializable


class SerializableBean : Serializable {
    var serializableField: InnerSerializableBean? = null
}

class InnerSerializableBean : Serializable {
    var commonBean: CommonBean? = null
}

class CommonBean{
    private var s: String = "abc"
}