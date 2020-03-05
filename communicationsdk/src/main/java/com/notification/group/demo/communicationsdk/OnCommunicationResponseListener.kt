package com.notification.group.demo.communicationsdk

interface OnCommunicationResponseListener {

     fun onSuccess(operationId: Int, responseProcessor: ICommunicationResponseProcessor?)

     fun onFailure(operationId: Int, exception: CommunicationException?)
}