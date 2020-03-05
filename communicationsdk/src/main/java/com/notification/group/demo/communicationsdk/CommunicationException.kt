package com.notification.group.demo.communicationsdk


class CommunicationException(
    override var message: String?,
    var code: Int,
    var statusCode: Int,
    var responseText: String?
) : Exception() {

    companion object {
        var IO_EXCEPTION = 2100
        var PROTOCOL_NOT_SUPPORTED = 2101
        var MALFORMED_URL = 2102
        var SOCKET_TIMEOUT = 2103
        var UNKNOWN = 2104
        var PAYLOAD_NULL = 2105
        var FORCE_ENDED = 2106
        var EOF_EXCEPTION = 2107
        var UNABLE_TO_PROCESS = 2108
        var PROCESSOR_IS_NULL = 2109

        var UNEXPECTED_RESPONSE = 2200
        var NO_RESPONSE = 2201
        var CONTENT_NOT_AVAILABLE = 2202
        var CONTENT_NOT_MODIFIED = 2203
    }
    private var _code: Int = code
    var _statusCode: Int = statusCode
    private var _responseText: String? = responseText


    fun isKnownException(): Boolean {
        return _code != UNKNOWN
    }


}