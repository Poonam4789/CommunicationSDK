package com.notification.group.demo.apicommunicationsdk

import com.notification.group.demo.communicationsdk.CommunicationHttpMethod
import com.notification.group.demo.communicationsdk.ICommunicationOperation
import com.notification.group.demo.communicationsdk.ICommunicationResponseProcessor
import com.notification.group.demo.communicationsdk.OnCommunicationResponseListener
import java.lang.ref.WeakReference

class HomeOperation(url: String, listener: OnCommunicationResponseListener) :ICommunicationOperation {
    private var _url: String? = url
    private var _listenerWeakRef: WeakReference<OnCommunicationResponseListener>? = null

    init {
        _listenerWeakRef = WeakReference(listener)
    }

    override fun getId(): Int { return 1001    }

    override fun getPath(): String? {
        return _url
    }

    override fun getMethod(): CommunicationHttpMethod {
        return CommunicationHttpMethod.GET
    }

    override fun getHeaders(): Map<String, String>? {
        return null
    }

    override fun shouldUseCache(): Boolean {
       return false
    }

    override fun getPayload(): String? {
        return null
    }

    override fun getProcessor(): ICommunicationResponseProcessor {
        return HomeResponseProcessor()
    }

    override fun getListener(): WeakReference<OnCommunicationResponseListener>? {
        return this._listenerWeakRef
    }

    override fun destroy() {
        _url = null
        _listenerWeakRef?.clear()
        _listenerWeakRef = null
    }
}