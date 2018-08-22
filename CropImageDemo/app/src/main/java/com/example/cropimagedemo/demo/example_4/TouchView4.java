package com.example.cropimagedemo.demo.example_4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;

import static android.graphics.Matrix.MTRANS_X;
import static android.graphics.Matrix.MTRANS_Y;

public class TouchView4 extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener {

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
    private Matrix mDisplayMatrix = new Matrix();

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

    public TouchView4(Context context) {
        super(context);
        init();
    }

    public TouchView4(Context context, @Nullable AttributeSet attrs) {
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
        mDisplayMatrix.postConcat(matrix);
        return mDisplayMatrix;
    }

    public void setRotation(float degress){
        float scale = getCurrentScale();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        //Matrix matrix = new Matrix();
        //matrix.postRotate(degress, mLayoutWidht / 2, mLayoutHeight / 2);
        //setImageMatrix(getImageMatrix(matrix));

        mBaseMatrix.postRotate(degress, mLayoutWidht / 2, mLayoutHeight / 2);
        setImageMatrix(mBaseMatrix);

//        Drawable drawable = getDrawable();
//
//        if(drawable != null) {
//            final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//            final Canvas canvas = new Canvas(bmp);
//            canvas.rotate(degress);
//            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//            drawable.draw(canvas);
//
//            setImageDrawable(drawable);
//            setImageMatrix(getImageMatrix(mBaseMatrix));
//            requestLayout();
//        }

        printMatrix(mBaseMatrix);
    }

    private float getCurrentScale(){
        float scalex = getMatrixValue(mBaseMatrix, Matrix.MSCALE_X);
        float skewy = getMatrixValue(mBaseMatrix, Matrix.MSKEW_Y);
        float scale = (float) Math.sqrt(scalex * scalex + skewy * skewy);
        return Math.abs(scale);
    }

    private class SimpleScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = getCurrentScale();
            float scaleFactor = detector.getScaleFactor();
            float targetScale = Math.min(scaleFactor, MAX_SCALE);

            Log.i(TAG, "Scale Info: " +"[" +
                    "Span : " + detector.getCurrentSpan() + ", " +
                    "Current Scale : " + scale + ", " +
                    "Scale Factor : " + scaleFactor + ", " +
                    "Target Scale : " + targetScale + "]");

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

    public void printMatrix(Matrix matrix) {
        float scalex = getMatrixValue(matrix, Matrix.MSCALE_X);
        float scaley = getMatrixValue(matrix, Matrix.MSCALE_Y);
        float tx = getMatrixValue(matrix, Matrix.MTRANS_X);
        float ty = getMatrixValue(matrix, Matrix.MTRANS_Y);
        float rx = getMatrixValue(matrix, Matrix.MPERSP_1);
        float ry = getMatrixValue(matrix, Matrix.MPERSP_2);
        Log.d(TAG, "matrix: { x: " + tx + ", y: " + ty + ", scalex: " + scalex + ", scaley: " + scaley + " rx "+ rx +", ry "+ ry +" }");
    }
}
