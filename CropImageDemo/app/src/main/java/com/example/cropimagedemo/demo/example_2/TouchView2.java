package com.example.cropimagedemo.demo.example_2;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;

public class TouchView2 extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener {

    /**
     * android developer doc : https://developer.android.com/reference/android/graphics/Matrix
     */

    private String TAG = "TouchView";

    private Matrix mMatrix = new Matrix();
    private Matrix mBaseMatrix = new Matrix();

    /*
     * { MSCALE_X, MSKEW_X, MTRANS_X}
     * { MSKEW_Y, MSCALE_Y,  MTRANS_Y}
     * { MPERSP_0, MPERSP_1,  MPERSP_2
     */

    private float[] mMatrixValues = new float[9];
    private float mMaxScale = 3f;
    private float mMinScale = 1f;

    private int mLayoutWidht;
    private int mLayoutHeight;

    private int mImgWidth;
    private int mImgHeight;

    private boolean mInitLayout;

    private ScaleGestureDetector mScaleDetector;
    private SimpleScaleListener mScaleListener;

    private boolean mIsScrollEnable = true;

    public TouchView2(Context context) {
        super(context);
        init();
    }

    public TouchView2(Context context, @Nullable AttributeSet attrs) {
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
            scale = Math.min((float) mImgWidth / mLayoutWidht, (float) mImgHeight / mLayoutWidht);

        mMinScale = Math.min(mMaxScale, scale);

        int px = (mLayoutWidht - mImgWidth) / 2;
        int py = (mLayoutHeight - mImgHeight) / 2;

        // center x, y
        mBaseMatrix.postTranslate(px, py);

        Matrix matrix = new Matrix();
        matrix.setScale(mMinScale, mMinScale, px, py);
        setImageMatrix(getImageMatrix(matrix));

        mInitLayout = true;
    }

    private Matrix getImageMatrix(Matrix matrix){
        mBaseMatrix.postConcat(matrix);
        return mBaseMatrix;
    }

    private class SimpleScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.i(TAG, "onScale: " + detector.getCurrentSpan());
            float curScale = getMatrixValue(mMatrix, Matrix.MSCALE_X);
            float scale = curScale * detector.getScaleFactor();
            float targetScale = Math.min(mMaxScale, scale);

            Matrix matrix = new Matrix();
            matrix.setScale(targetScale, targetScale, mLayoutWidht / 2, mLayoutHeight / 2);
            setImageMatrix(getImageMatrix(matrix));
            return true;
        }
    }
}
