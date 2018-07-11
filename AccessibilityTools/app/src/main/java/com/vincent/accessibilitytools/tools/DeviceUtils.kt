package com.vincent.accessibilitytools.tools

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import kotlin.jvm.internal.ClassReference

object DeviceUtils {

    val TAG : String = "DeviceUtils"

    val PERMISSION_REQUEST : Int = 1

    // No explanation needed, we can request the permission.
    fun requestPermission(activity : Activity, permissions: Array<String>) {
        ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST)
    }

    fun showRequestPermission(activity : Activity, permission : String){
        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    fun hasPermission(context: Context, permissions: Array<out String>) : Boolean {
        val accept : MutableList<Boolean> = mutableListOf()
        for (permission in permissions){
            if(ContextCompat.checkSelfPermission(context, permission) ==  PackageManager.PERMISSION_GRANTED){
                accept.add(true)
            }
        }

        return accept.size == permissions.size
    }

    fun isAccessibilityEnabled(context: Context, clazz: Any): Boolean {

        var accessibilityEnabled = 0
        val packageName = context.packageName
        val className = (clazz as ClassReference).jClass.name
        val serviceName = "$packageName/$className"

        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.applicationContext.contentResolver,
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED)

            Log.v(TAG, "accessibilityEnabled = $accessibilityEnabled")

        } catch (e: Settings.SettingNotFoundException) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.message)
        }

        val stringColonSplitter = TextUtils.SimpleStringSplitter(':')

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "Accessibility is enabled.")

            val settingValue = Settings.Secure.getString(
                    context.applicationContext.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (settingValue != null) {
                stringColonSplitter.setString(settingValue)

                while (stringColonSplitter.hasNext()) {
                    val accessibilityService = stringColonSplitter.next()
                    Log.v(TAG, ">>>> accessibilityService :: $accessibilityService $serviceName")
                    if (accessibilityService.equals(serviceName, ignoreCase = true)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!")
                        return true
                    }
                }
            }
        } else {
            Log.v(TAG, "Not found $serviceName accessibility.")
        }

        return false
    }
}