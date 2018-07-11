package com.vincent.accessibilitytools.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.FingerprintGestureController
import android.accessibilityservice.FingerprintGestureController.*
import android.accessibilityservice.GestureDescription
import android.app.KeyguardManager
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.fingerprint.FingerprintManager
import android.net.Uri
import android.os.SystemClock
import android.provider.Settings
import android.view.*
import android.view.accessibility.AccessibilityManager
import android.widget.Toast

class FingerprintAccessibilityService : AccessibilityService(){

    private val TAG: String = "GestureService"
    private var mGestureController: FingerprintGestureController? = null
    private var mFingerprintGestureCallback: FingerprintGestureController.FingerprintGestureCallback? = null

    var mKeyguardMgr : KeyguardManager? = null
    var mFingerPrintMgr : FingerprintManager? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate()")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.i(TAG, "onAccessibilityEvent()")

        val type = event.eventType
        when(type){
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> Log.i(TAG, "TYPE_WINDOW_STATE_CHANGED")
        }

        val e = MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN,
                100f, 100f, 0)

//        if (!Settings.canDrawOverlays(this)) {
//            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
//            startActivity(intent)
//        } else {
//            val lp = WindowManager.LayoutParams(windowType, windowFlag, windowFormat)
//            pointerView!!.setBackgroundColor(Color.RED)
//
//            windowMgr.addView(pointerView, lp)
//            pointerView!!.dispatchTouchEvent(e)
//        }

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "onServiceConnected()")

        mKeyguardMgr = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        mFingerPrintMgr = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        mFingerPrintMgr.authenticate()

        mGestureController = fingerprintGestureController
        if (mGestureController != null
                && mFingerprintGestureCallback == null
                && mGestureController!!.isGestureDetectionAvailable) {

            mGestureController.run {
                Log.i(TAG, "Fingerprint run.")
            }

            mFingerprintGestureCallback = object : FingerprintGestureController.FingerprintGestureCallback() {
                override fun onGestureDetected(gesture: Int) {
                    when (gesture) {
                        FINGERPRINT_GESTURE_SWIPE_DOWN -> Log.i(TAG, "FINGERPRINT_GESTURE_SWIPE_DOWN")
                        FINGERPRINT_GESTURE_SWIPE_LEFT -> Log.i(TAG, "FINGERPRINT_GESTURE_SWIPE_LEFT")
                        FINGERPRINT_GESTURE_SWIPE_RIGHT -> Log.i(TAG, "FINGERPRINT_GESTURE_SWIPE_RIGHT")
                        FINGERPRINT_GESTURE_SWIPE_UP -> Log.i(TAG, "FINGERPRINT_GESTURE_SWIPE_UP")
                        else -> Log.i(TAG, "Error: Unknown gesture type detected!")
                    }
                }

                override fun onGestureDetectionAvailabilityChanged(available: Boolean) {
                    Log.i(TAG, "onGestureDetectionAvailabilityChanged($available)")
                }
            }

            val handler = Handler()
            val runnable = Runnable {
                Log.i(TAG, "Fingerprint Callback.")
            }

            handler.post(runnable)

            mGestureController?.registerFingerprintGestureCallback(
                    mFingerprintGestureCallback, handler)
        }
    }

    override fun onInterrupt() {
        Log.i(TAG, "onInterrupt()")
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mGestureController != null && mFingerprintGestureCallback != null){
            mGestureController?.unregisterFingerprintGestureCallback(mFingerprintGestureCallback)
        }
        Log.i(TAG, "onDestroy()")
    }

    override fun onGesture(gestureId: Int): Boolean {
        Log.i(TAG, "onGesture($gestureId)")
        return super.onGesture(gestureId)
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        Log.i(TAG, "onKeyEvent($event)")
        return super.onKeyEvent(event)
    }

    class FingerprintCallback(var context: Context) : FingerprintManager.AuthenticationCallback() {

        /**
         * This method will trigger one of a number of callback events
         * depending on the success or failure of the authentication.
         */
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            Toast.makeText(context, "onAuthenticationError", Toast.LENGTH_SHORT).show()
        }

        override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
            Toast.makeText(context, "onAuthenticationSucceeded", Toast.LENGTH_SHORT).show()
        }

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
            Toast.makeText(context, "onAuthenticationHelp", Toast.LENGTH_SHORT).show()
        }

        override fun onAuthenticationFailed() {
            Toast.makeText(context, "onAuthenticationFailed", Toast.LENGTH_SHORT).show()
        }
    }

}