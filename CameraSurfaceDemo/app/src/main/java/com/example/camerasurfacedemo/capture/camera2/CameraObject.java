package com.example.camerasurfacedemo.capture.camera2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Size;
import android.view.SurfaceHolder;

import com.example.camerasurfacedemo.capture.CameraInterface;

import java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraObject implements CameraInterface {

    /**
     * support api 21 above.
     */
    private Context mContext;
    private CameraManager mCameraMgr;
    private CameraDevice mCameraDevice;
    private SurfaceHolder mSurfaceHolder;
    private String mCameraID;
    private Size mCameraSize;
    private Boolean mHasFlash;
    private HandlerThread mCameraHandler;
    private Handler mOpenHanlder;
    private Handler mCaptureHanlder;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraCharacteristics mCameraCharacteristics;

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            if (null != mCameraDevice) {
                mCameraDevice.close();
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    };

    public CameraObject(Context context) {
        mContext = context;

        mCameraHandler = new HandlerThread("Camera2");
        mCameraHandler.start();

        mOpenHanlder = new Handler(mCameraHandler.getLooper());
        mCaptureHanlder = new Handler(mCameraHandler.getLooper());
    }

    /**
     * CameraObject.CameraInfo.CAMERA_FACING_FRONT
     * CameraObject.CameraInfo.CAMERA_FACING_BACK
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void openCamera(CameraType type) {
        if (hasCameraPermission(mContext)) {
            mCameraMgr = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            try {
                for (String cameraId : mCameraMgr.getCameraIdList()) {
                    mCameraCharacteristics = mCameraMgr.getCameraCharacteristics(cameraId);
                    mCameraID = cameraId;

                    if (mCameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                            Camera.CameraInfo.CAMERA_FACING_FRONT && type == CameraType.FONT) {

                        StreamConfigurationMap map = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        mCameraSize = map.getOutputSizes(SurfaceTexture.class)[0];
                        mHasFlash = mCameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                        break;
                    }
                }

                mCameraMgr.openCamera(mCameraID, mStateCallback, mOpenHanlder);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        } else {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void takePhoto() {
        try {
            //final CaptureRequest.Builder previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //previewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
            mCameraDevice.createCaptureSession(Arrays.asList(
                    mSurfaceHolder.getSurface(),
                    mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice){
                        return;
                    }

                    mCameraCaptureSession = cameraCaptureSession;
                    try {
                        // previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        // 显示预览
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        mCameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                }
            }, mCaptureHanlder);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSurfaceHolder(SurfaceHolder holder) {
        mSurfaceHolder = holder;
    }

    private boolean hasCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}
