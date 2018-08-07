package com.example.camerasurfacedemo.camera.support;

import android.content.Context;
import android.hardware.Camera;

import com.example.camerasurfacedemo.camera.CameraCompat;

public class CameraObject implements CameraCompat {

    private Context mContext;
    private Camera mCamera;
    private int mCameraId;

    public CameraObject(Context context){
        mContext = context;
    }

    /**
     *
     * @param type
     * 1. Camera.CameraInfo.CAMERA_FACING_FRONT
     * 2. Camera.CameraInfo.CAMERA_FACING_BACK
     * @return
     */
    public void initCamera(int type) {
        if(type == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            int numCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numCameras; i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCamera = Camera.open(i);
                    mCameraId = info.facing;
                    break;
                }
            }
        }

        if (mCamera == null) {
            mCamera = Camera.open();
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    @Override
    public void openCamera() {

    }
}
