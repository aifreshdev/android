package com.example.camerasurfacedemo.capture;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private CameraInterface mCameraInterface;

    public CameraSurfaceView(Context context) {
        super(context);
        mCameraInterface = new CameraBuilder(context).getCamera();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCameraInterface.setSurfaceHolder(surfaceHolder);
        mCameraInterface.openCamera(CameraInterface.CameraType.BACK);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mCameraInterface.setSurfaceHolder(surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCameraInterface.setSurfaceHolder(surfaceHolder);
    }
}
