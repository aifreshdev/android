package com.vincent.accessibilitytools.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.vincent.accessibilitytools.R
import com.vincent.accessibilitytools.service.FingerprintAccessibilityService
import com.vincent.accessibilitytools.tools.DeviceUtils
import com.vincent.fingerprintgesture.service.WindowGestureService

class MainActivity : AppCompatActivity() {

    val PERMISSION_OVERLAY_REQUEST = 1
    val mPermssions : Array<String> = arrayOf(Manifest.permission.USE_FINGERPRINT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(DeviceUtils.hasPermission(this, mPermssions)) {
            if (DeviceUtils.isAccessibilityEnabled(this, FingerprintAccessibilityService::class)) {
//                checkDrawOverlayPermission()
            } else {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }else{
            DeviceUtils.requestPermission(this, mPermssions)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            DeviceUtils.PERMISSION_REQUEST ->
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startService(Intent(this, WindowGestureService::class.java));
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (PERMISSION_OVERLAY_REQUEST == requestCode) {
            if (Settings.canDrawOverlays(this)) {
                startService(Intent(this, WindowGestureService::class.java))
            }
        }
    }

    fun checkDrawOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, PERMISSION_OVERLAY_REQUEST)
        } else {
            startService(Intent(this, WindowGestureService::class.java))
        }
    }



}
