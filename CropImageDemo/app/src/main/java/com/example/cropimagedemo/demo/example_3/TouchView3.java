package com.example.cropimagedemo.demo.example_3;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;

public class TouchView3 extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener {

    /**
     * android developer doc : https://developer.android.com/reference/android/graphics/Matrix
     */

    private String TAG = "TouchView";

    /*
     * { MSCALE_X, MSKEW_X, MTRANS_X}
     * { MSKEW_Y, MSCALE_Y,  MTRANS_Y}
     * { MPERSP_0, MPERSP_1,  MPERSP_2
     */

    private float[] mMatrixValues = new float[9];
    private Matrix mBaseMatrix = new Matrix();

    private float MAX_SCALE = 3f;
    private float DEFAULT_SCALE = 1f;

    private int mLayoutWidht;
    private int mLayoutHeight;

    private int mImgWidth;
    private int mImgHeight;

    private boolean mInitLayout;

    private ScaleGestureDetector mScaleDetector;
    private SimpleScaleListener mScaleListener;

    private boolean mIsScrollEnable = true;

    public TouchView3(Context context) {
        super(context);
        init();
    }

    public TouchView3(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);

        mScaleListener = new SimpleScaleListener();
        mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private float getMatrixValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    @Override
    public void onGlobalLayout() {
        if(mInitLayout){
            return;
        }

        Drawable drawable = getDrawable();
        if(drawable == null){
            return;
        }

        mLayoutWidht = getWidth();
        mLayoutHeight = getHeight();

        mImgWidth = drawable.getIntrinsicWidth();
        mImgHeight = drawable.getIntrinsicHeight();

        float scale = 1f;

        if (mImgWidth > mLayoutWidht && mImgHeight <= mLayoutHeight)
            scale = (float) mLayoutWidht / mImgWidth;

        if (mImgHeight > mLayoutHeight && mImgWidth <= mLayoutWidht)
            scale = (float) mLayoutHeight / mImgHeight;

        // if drawable biggest than layout size get min scale
        if (mImgWidth > mLayoutWidht && mImgHeight > mLayoutHeight)
            scale = Math.min((float) mImgWidth / mLayoutWidht, (float) mImgHeight / mLayoutHeight);

        DEFAULT_SCALE = scale;

        int px = (mLayoutWidht - mImgWidth) / 2;
        int py = (mLayoutHeight - mImgHeight) / 2;

        // center x, y
        mBaseMatrix.postTranslate(px, py);
        mBaseMatrix.postScale(scale, scale, px, py);
        setImageMatrix(mBaseMatrix);

        mInitLayout = true;
    }

    /**
     * add new matrix concat origin matrix
     * @param matrix
     * @return
     */
    private Matrix getImageMatrix(Matrix matrix){
        mBaseMatrix.postConcat(matrix);
        return mBaseMatrix;
    }

    private float getCurrentScale(){
        return getMatrixValue(mBaseMatrix, Matrix.MSCALE_X);
    }

    private class SimpleScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.i(TAG, "onScale: " + detector.getScaleFactor() + ", " + detector.getCurrentSpan());
            float scale = getCurrentScale();
            float scaleFactor = detector.getScaleFactor();
            float targetScale = Math.min(scaleFactor, MAX_SCALE);

            Log.i(TAG, "onScale 2: " + scale + ", " + scaleFactor + ", " + targetScale);

            // DEFAULT_SCALE ~ MAX_SCALE ranage
            if((scale < MAX_SCALE && scaleFactor > 1f) /* zoom out */
                    || (scale > DEFAULT_SCALE && scaleFactor < 1f) /* zoom in */) {

                if (scale * scaleFactor < DEFAULT_SCALE) {
                    targetScale = DEFAULT_SCALE / scale;
                } else if (scale * scaleFactor > MAX_SCALE) {
                    targetScale = MAX_SCALE / scale;
                }

                mBaseMatrix.postScale(targetScale, targetScale, mLayoutWidht / 2, mLayoutHeight / 2);
                setImageMatrix(mBaseMatrix);
            }

            return true;
        }
    }
}
