package com.example.camerasurfacedemo.capture.camera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.example.camerasurfacedemo.capture.CameraInterface;

public class CameraObject implements CameraInterface {

    private Context mContext;
    private android.hardware.Camera mCamera;
    private int mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;

    public CameraObject(Context context) {
        mContext = context;
    }

    /**
     * @param
     * 1. CameraObject.CameraInfo.CAMERA_FACING_FRONT
     * 2. CameraObject.CameraInfo.CAMERA_FACING_BACK
     * @return
     */

    @Override
    public void openCamera(CameraType type) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int numCameras = android.hardware.Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            mCameraID = info.facing;

            if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT
                    && type == CameraType.FONT) {
                mCamera = Camera.open(i); break;
            }
        }

        if (mCamera == null) {
            mCamera = Camera.open();
            mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    @Override
    public void takePhoto() {


    }

    @Override
    public void setSurfaceHolder(SurfaceHolder holder) {

    }
}
