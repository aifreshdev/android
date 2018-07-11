package com.vincent.fingerprintgesture.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.view.*
import com.vincent.accessibilitytools.view.PointerView

class WindowGestureService : Service() {

    val TAG: String = "WindowGestureService"

    val windowMgr: WindowManager by lazy {
        getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    var windowType: Int = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    var windowFlag: Int = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE // no input focu
    var windowFormat: Int = PixelFormat.TRANSLUCENT
    val pointerView = PointerView(this)
    val windowBinder = WindowBinder()

    inner class WindowBinder : Binder() {
        fun getService() : WindowGestureService {
            return this@WindowGestureService
        }
    }
    override fun onBind(intent: Intent?): IBinder = windowBinder

    override fun onCreate() {
        Log.i(TAG, ">>> onCreate")
        super.onCreate()

        val pointerView = PointerView(this)
        val lp = WindowManager.LayoutParams(windowType, windowFlag, windowFormat)
        lp.gravity = Gravity.TOP or Gravity.LEFT
        lp.width = 100
        lp.height = 100

        pointerView.setBackgroundColor(Color.RED)
        windowMgr.addView(pointerView, lp)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, ">>> onStartCommand")
        return Service.START_STICKY
    }

    fun getView() : PointerView? {
        return pointerView
    }

}