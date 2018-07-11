package project.example.arcoreplanerenderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import project.example.arcoreplanerenderer.common.helpers.CameraPermissionHelper;
import project.example.arcoreplanerenderer.common.helpers.DisplayRotationHelper;
import project.example.arcoreplanerenderer.common.helpers.FullScreenHelper;
import project.example.arcoreplanerenderer.common.helpers.SnackbarHelper;
import project.example.arcoreplanerenderer.common.helpers.TapHelper;
import project.example.arcoreplanerenderer.common.rendering.BackgroundRenderer;
import project.example.arcoreplanerenderer.common.rendering.ObjectRenderer;
import project.example.arcoreplanerenderer.common.rendering.PlaneRenderer;
import project.example.arcoreplanerenderer.common.rendering.PointCloudRenderer;

import static com.google.ar.core.Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL;
import static com.google.ar.core.Config.UpdateMode.LATEST_CAMERA_IMAGE;

public class PlaneRendererActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    private final String TAG = "PlaneRendererActivity";

    private final BackgroundRenderer mBGRenderer = new BackgroundRenderer();
    private final PointCloudRenderer mPointCloudRenderer = new PointCloudRenderer();
    private final PlaneRenderer mPlaneRenderer = new PlaneRenderer();
    private final ObjectRenderer mVirtualObject = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectShadow = new ObjectRenderer();

    private DisplayRotationHelper mDisplayRotationHelper; // for update session that will show camera surface

    private GLSurfaceView mGLSurfaceView;
    private Session mArSession;
    private Config mConfig;
    private TapHelper mTapHelper;
    private boolean mInstallRequested;
    private final SnackbarHelper mInfoHelper = new SnackbarHelper();

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plane_renderer);

        mDisplayRotationHelper = new DisplayRotationHelper(/*context=*/ this);
        mGLSurfaceView = findViewById(R.id.surfaceview);
        mGLSurfaceView.setPreserveEGLContextOnPause(true);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        mGLSurfaceView.setRenderer(this);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        mTapHelper = new TapHelper(this);
        mGLSurfaceView.setOnTouchListener(mTapHelper);

        mInstallRequested = false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG).show();

            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mArSession != null){
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            mDisplayRotationHelper.onPause();
            mGLSurfaceView.onPause();
            mArSession.pause();
        }
    }

    private void initSession(){
        if (mArSession == null) {
            Exception exception = null;
            String errorMsg = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !mInstallRequested)) {
                    case INSTALL_REQUESTED:
                        mInstallRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }


                // Create the session.
                mArSession = new Session(/* context= */ this);

                mConfig = new Config(mArSession);
                //mConfig.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
                mConfig.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL);
                //mConfig.setLightEstimationMode(Config.LightEstimationMode.DISABLED);

                mArSession.configure(mConfig);

            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                errorMsg = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                errorMsg = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                errorMsg = "Please update this app";
                exception = e;
            } catch (UnavailableDeviceNotCompatibleException e) {
                errorMsg = "This device does not support AR";
                exception = e;
            } catch (Exception e) {
                errorMsg = "Failed to create AR session";
                exception = e;
            }

            if (errorMsg != null) {
                mInfoHelper.showError(this, errorMsg);
                Log.e(TAG, "Exception creating session", exception);
                return;
            }
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            mArSession.resume();
        } catch (CameraNotAvailableException e) {
            // In some cases (such as another camera app launching) the camera may be given to
            // a different app instead. Handle this properly by showing a message and recreate the
            // session at the next iteration.
            mInfoHelper.showError(this, "Camera not available. Please restart the app.");
            mArSession = null;
            return;
        }

        mGLSurfaceView.onResume();
        mDisplayRotationHelper.onResume();

        mInfoHelper.showMessage(this, "Searching for surfaces...");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        try{

            mBGRenderer.createOnGlThread(this);
            mPlaneRenderer.createOnGlThread(this, "models/trigrid.png");
            mPointCloudRenderer.createOnGlThread(this);
            //mVirtualObject.createOnGlThread( this, "models/andy.obj", "models/andy.png");
            //mVirtualObject.setMaterialProperties(0.0f, 2.0f, 0.5f, 6.0f);

        }catch (Exception e){
            Log.e(TAG, "Failed to read an asset file", e);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mDisplayRotationHelper.onSurfaceChanged(width, height);
    }

    /**
     *
     * @param gl
     * Draw any camera layer
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (mArSession == null) {
            Log.i(TAG, "Not init Session yet.");
            return;
        }

        // mArSession,setDisplayGeometry. it can show camera surface
        mDisplayRotationHelper.updateSessionIfNeeded(mArSession);

        try {
            mArSession.setCameraTextureName(mBGRenderer.getTextureId());

            Frame frame = mArSession.update();
            Camera camera = frame.getCamera();
            //frame.getUpdatedTrackables(Point.class);

            // Handle taps. Handling only one tap per frame, as taps are usually low frequency
            // compared to frame rate.
            MotionEvent tap = mTapHelper.poll();
            if (tap != null && camera.getTrackingState() == TrackingState.TRACKING) {
                Log.i(TAG, "Tap : TRACKING");
            }

            mBGRenderer.draw(frame);

            // If not tracking, don't draw 3d objects.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                Log.i(TAG, "Tap : PAUSED");
                return;
            }

            // Get projection matrix.
            float[] projmtx = new float[16];
            camera.getProjectionMatrix(projmtx, 0, 0.5f, 100f);

            // Get camera matrix and draw.
            float[] viewmtx = new float[16];
            camera.getViewMatrix(viewmtx, 0);

            // Compute lighting from average intensity of the image.
            // The first three components are color scaling factors.
            // The last one is the average pixel intensity in gamma space.
            final float[] colorCorrectionRgba = new float[4];
            frame.getLightEstimate().getColorCorrection(colorCorrectionRgba, 0);

            // Visualize tracked points.
            PointCloud pointCloud = frame.acquirePointCloud();
            mPointCloudRenderer.update(pointCloud);
            mPointCloudRenderer.draw(viewmtx, projmtx);

            // Application is responsible for releasing the point cloud resources after
            // using it.
            pointCloud.release();

            // Check if we detected at least one plane. If so, hide the loading message.
            if (mInfoHelper.isShowing()) {
                for (Plane plane : mArSession.getAllTrackables(Plane.class)) {
                    if (plane.getType() == com.google.ar.core.Plane.Type.HORIZONTAL_UPWARD_FACING
                            && plane.getTrackingState() == TrackingState.TRACKING) {
                        mInfoHelper.hide(this);
                        break;
                    }
                }
            }

            // Visualize planes.
            mPlaneRenderer.drawPlanes(
                    mArSession.getAllTrackables(Plane.class), camera.getDisplayOrientedPose(), projmtx);


        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }
    }
}
