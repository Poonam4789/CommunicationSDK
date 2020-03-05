package com.notification.group.demo.communicationsdk

import android.os.AsyncTask
import android.util.Log
import java.lang.ref.WeakReference

open class CommunicationAsyncTask(operation :ICommunicationOperation, listener:OnCommunicationCompletionListener) : AsyncTask<Void, Void, Boolean>() {

    private var TAG :String ="CommunicationAsyncTask"
    private var _operation: ICommunicationOperation? = operation
    private var _listenerWeakRef: WeakReference<OnCommunicationCompletionListener>? = WeakReference(listener)
    private var _communicator: Communicator? = null
    open var _processor: ICommunicationResponseProcessor? = null
    open var _exception: CommunicationException? = null



    fun getOperation(): ICommunicationOperation? {
        return _operation
    }

    fun begin() {
        Log.d(TAG,"begin")
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    fun end() {
        if (_communicator != null) {
            _communicator?.end()
        }
        cancel(true)
    }

    fun destroy() {
        _communicator?.destroy()
        _communicator = null

        _operation?.destroy()
        _operation = null

        _listenerWeakRef?.clear()
        _listenerWeakRef = null

        _processor = null
        _exception = null
    }

    fun notifyResponse(result:Boolean){
        if(_operation?.getListener()!=null && _operation?.getListener()?.get()!=null){
            var listener = _operation?.getListener()?.get()
            if(result){
                listener?.onSuccess(_operation?.getId()!!,_processor);
            }else{
                listener?.onFailure(_operation?.getId()!!,_exception)
            }
        }
    }

    private fun notifyCompletion(result: Boolean) {
        if (_listenerWeakRef != null && _listenerWeakRef?.get() != null) {
            _listenerWeakRef?.get()!!.onComplete(_operation?.getId()!!, result)
        }
    }

    override fun doInBackground(vararg p0: Void?): Boolean {
        _communicator = Communicator()
        var result :Boolean = true

        try {
            _processor = _communicator?.call(_operation)
        } catch (ex: CommunicationException) {
            _exception = ex
            result = java.lang.Boolean.FALSE
        }
        return result
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        result?.let { notifyResponse(it) }
        result?.let { notifyCompletion(it) }
    }
}