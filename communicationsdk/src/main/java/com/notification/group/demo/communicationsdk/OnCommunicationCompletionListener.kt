package com.notification.group.demo.communicationsdk

interface OnCommunicationCompletionListener {
  fun onComplete(operationId: Int,success:Boolean);
}