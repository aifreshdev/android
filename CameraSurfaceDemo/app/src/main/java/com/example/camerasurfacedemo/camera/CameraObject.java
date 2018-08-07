package com.example.camerasurfacedemo.camera;

import android.content.Context;
import android.hardware.camera2.CameraManager;

public class CameraObject implements CameraCompat {

    private Context mContext;
    /**
     * support api 21 above.
     */
    private CameraManager mCameraMgr;

    public CameraObject(Context context){
        mContext = context;
    }

    /**
     * @param type 1. Camera.CameraInfo.CAMERA_FACING_FRONT
     *             2. Camera.CameraInfo.CAMERA_FACING_BACK
     * @return
     */
    public void openCamera(int type) {
        mCameraMgr = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    public void openCamera() {

    }
}
