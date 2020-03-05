package com.notification.group.demo.communicationsdk

interface ICommunicationResponseProcessor {
    @Throws(CommunicationException::class)
    abstract fun process(
        statusCode: Int,
        responseText: String,
        responseHeaders: Map<String, List<String>>
    ): Boolean
}