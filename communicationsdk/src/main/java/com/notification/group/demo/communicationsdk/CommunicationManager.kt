package com.notification.group.demo.communicationsdk

import android.content.Context
import android.util.Log
import java.util.*

class CommunicationManager(context: Context) : OnCommunicationCompletionListener {

    private var TAG :String ="CommunicationManager"
    companion object {
        private var ourInstance: CommunicationManager? = null

        fun initialize(context: Context) {
            if (ourInstance == null) {
                ourInstance = CommunicationManager(context)
            }
        }

        fun getInstance(): CommunicationManager? {
            return ourInstance
        }

    }

    private var _connectivityChecker: CommunicationConnectivityChecker
    private val _operationsQueue = ArrayList<ICommunicationOperation>()
    private var _currentTask: CommunicationAsyncTask? = null

    init {
        _connectivityChecker = CommunicationConnectivityChecker(context)
        Communicator.initCookieManager()
        Communicator.trustAllSSLCertificates()
        Communicator.installHttpResponseCache(context)
    }

    fun performOperation(operation: ICommunicationOperation) {
        synchronized(_operationsQueue) {
            Log.d(TAG,"performOperation")
            _operationsQueue.add(operation)
        }
        processQueue()
    }

    fun abortOperation(operation: ICommunicationOperation) {
        if (_currentTask != null && _currentTask?.getOperation()?.getId() == operation.getId()) {
            _currentTask?.end()
            clearCurrentTask()
            processQueue()
        } else {
            removeOperationFromQueue(operation)
        }
    }

    fun abortAllOperations() {
        if (_currentTask != null) {
            _currentTask?.end()
            clearCurrentTask()
        }
        synchronized(_operationsQueue) {
            _operationsQueue.clear()
        }
    }

    private fun processQueue() {
        if (_currentTask == null && _connectivityChecker.isConnected()) {
            synchronized(_operationsQueue) {
                if (_operationsQueue.size > 0) {
                    Log.d(TAG,"processQueue")
                    val operation = _operationsQueue.removeAt(0)
                    _currentTask = CommunicationAsyncTask(operation, this)
                    _currentTask?.begin()
                }
            }
        }
    }

    private fun clearCurrentTask() {
        _currentTask?.destroy()
        _currentTask = null
    }

    private fun removeOperationFromQueue(operation: ICommunicationOperation) {
        synchronized(_operationsQueue) {
            val iterator = _operationsQueue.iterator()
            while (iterator.hasNext()) {
                val operation1 = iterator.next()
                if (operation1.getId() == operation.getId()) {
                    iterator.remove()
                }
            }
        }
    }

    override fun onComplete(operationId: Int, success: Boolean) {
        clearCurrentTask()
        processQueue()
    }
}