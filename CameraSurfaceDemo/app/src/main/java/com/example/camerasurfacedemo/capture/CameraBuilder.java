package com.example.camerasurfacedemo.capture;

import android.content.Context;
import android.os.Build;

import com.example.camerasurfacedemo.capture.camera2.CameraObject;

public class CameraBuilder {

    private Context mContext;
    private android.graphics.Camera camera;
    private int mCameraId = -1;

    public CameraBuilder(Context context){
        mContext = context;
    }

    public CameraInterface getCamera(){
        if(hasLollipop()){
            return new CameraObject(mContext);
        }else {
            return new com.example.camerasurfacedemo.capture.camera.CameraObject(mContext);
        }
    }

    private boolean hasLollipop(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}
