package com.notification.group.demo.apicommunicationsdk

import com.notification.group.demo.communicationsdk.CommunicationException
import com.notification.group.demo.communicationsdk.ICommunicationResponseProcessor
import org.json.JSONException
import org.json.JSONObject

class HomeResponseProcessor :ICommunicationResponseProcessor {
    override fun process(
        statusCode: Int,
        responseText: String,
        responseHeaders: Map<String, List<String>>
    ): Boolean {
        try {
            val jsonObject = JSONObject(responseText)
        } catch (ex: JSONException) {
            throw CommunicationException(
                "Unexpected response",
                CommunicationException.UNEXPECTED_RESPONSE,
                statusCode,
                responseText
            )
        }

        return true
    }
}