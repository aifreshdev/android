package com.vincent.accessibilitytools.view

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View

class PointerView(context : Context) : View(context) {

    val TAG : String = "PointerView"

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i(TAG, "onTouchEvent : $event")
        return true
    }
}