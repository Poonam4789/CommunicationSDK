package com.notification.group.demo.communicationsdk

import java.lang.ref.WeakReference

interface ICommunicationOperation {

    fun getId():Int
    fun getPath(): String?
    fun getMethod() :CommunicationHttpMethod
    fun getHeaders() : Map<String, String>?
    fun shouldUseCache():Boolean
    fun getPayload(): String?
    fun getProcessor():ICommunicationResponseProcessor
    fun getListener(): WeakReference<OnCommunicationResponseListener>?
    fun destroy()

}