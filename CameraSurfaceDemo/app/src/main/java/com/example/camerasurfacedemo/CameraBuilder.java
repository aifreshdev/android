package com.example.camerasurfacedemo;

import android.content.Context;
import android.graphics.Camera;
import android.os.Build;

import com.example.camerasurfacedemo.camera.CameraCompat;
import com.example.camerasurfacedemo.camera.support.CameraObject;

public class CameraBuilder {

    private Context mContext;
    private Camera camera;
    private int mCameraId = -1;

    public CameraBuilder(Context context){
        mContext = context;
    }

    public CameraCompat getCamera(){
        if(hasLollipop()){
            return new CameraObject(mContext);
        }else {
            return new com.example.camerasurfacedemo.camera.support.CameraObject(mContext);
        }
    }

    private boolean hasLollipop(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}
