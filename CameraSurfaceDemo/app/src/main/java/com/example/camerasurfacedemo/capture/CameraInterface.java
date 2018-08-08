package com.example.camerasurfacedemo.capture;

import android.view.SurfaceHolder;

public interface CameraInterface {
    public void openCamera(CameraType type);
    public void takePhoto();
    public void setSurfaceHolder(SurfaceHolder holder);

    public enum CameraType{
        FONT(0),
        BACK(1);

        int type;
        CameraType(int type){
            this.type = type;
        }
    }
}
