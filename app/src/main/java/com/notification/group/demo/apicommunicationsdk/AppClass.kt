package com.notification.group.demo.apicommunicationsdk

import android.app.Application
import com.notification.group.demo.communicationsdk.CommunicationManager

class AppClass :Application() {

    override fun onCreate() {
        super.onCreate()
        CommunicationManager.initialize(this);
    }
}