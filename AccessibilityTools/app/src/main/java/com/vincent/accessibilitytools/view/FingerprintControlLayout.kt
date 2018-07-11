package com.vincent.accessibilitytools.view

import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import java.util.jar.Attributes

class FingerprintControlLayout(context : Context) : ViewGroup(context) {

    constructor(context : Context, attrs: Attributes) : this(context){

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

    }

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return super.onInterceptHoverEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

}