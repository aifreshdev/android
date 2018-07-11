package com.vincent.accessibilitytools.activity

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.util.Log
import com.vincent.accessibilitytools.service.KeepAliveService

class AppDelegate : Application() {

    private val TAG : String = "AppDelegate"

    override fun onCreate() {
        super.onCreate()
        Looper.myQueue().addIdleHandler {
            Log.i(TAG, "addIdleHandler KeepAliveService execute.")
            val keepAliveIntent = Intent(this, KeepAliveService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(keepAliveIntent)
            } else {
                startService(keepAliveIntent)
            }
            false
        }
    }
}