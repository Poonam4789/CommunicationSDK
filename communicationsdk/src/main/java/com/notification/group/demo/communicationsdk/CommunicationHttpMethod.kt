package com.notification.group.demo.communicationsdk

enum class CommunicationHttpMethod(var value: String) {
    GET("GET"),
    POST("POST"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    PUT("PUT"),
    DELETE("DELETE"),
    TRACE("TRACE");

    var _value: String = value

}