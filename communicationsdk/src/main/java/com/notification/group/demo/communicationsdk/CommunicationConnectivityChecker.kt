package com.notification.group.demo.communicationsdk

import android.content.Context
import android.net.ConnectivityManager

class CommunicationConnectivityChecker(context: Context) {
    var _context: Context = context

    fun isConnected(): Boolean {
        try {

            val connectivityManager =
                _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo!!.isAvailable && networkInfo.isConnected
        } catch (e: Exception) {
            return false
        }
    }
}