package com.example.cropimagedemo.demo.example_5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;

public class TouchView5 extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener {

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
    private Matrix mSaveMatrix = new Matrix();

    private float MAX_SCALE = 3f;
    private float DEFAULT_SCALE = 1f;

    private int mLayoutWidht;
    private int mLayoutHeight;

    private int mImgWidth;
    private int mImgHeight;


    // float: Array of points to draw [x0 y0 x1 y1 x2 y2 ...]
    // must contain at least 4 values
    private float[] mGridLines;
    private int mGridLineRow = 3;
    private int mGridLineCol = 3;
    private Paint mGridLinePaint;

    private boolean mInitLayout;

    private ScaleGestureDetector mScaleDetector;
    private SimpleScaleListener mScaleListener;


    public TouchView5(Context context) {
        super(context);
        init();
    }

    public TouchView5(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);

        mScaleListener = new SimpleScaleListener();
        mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);

        int rowPts = (mGridLineRow - 1) * 4;
        int colPts = (mGridLineRow - 1) * 4;

        mGridLines = new float[rowPts + colPts];
        mGridLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridLinePaint.setColor(Color.RED);
        mGridLinePaint.setStyle(Paint.Style.STROKE);
        mGridLinePaint.setStrokeWidth(1);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrid(canvas);
    }

    private void drawGrid(Canvas canvas) {
        int index = 0;
        int rowWidth = mLayoutWidht / mGridLineRow;
        int colWidth = mLayoutHeight / mGridLineCol;

        for (int i = 0; i < mGridLineRow - 1; i++) {
            int dx = rowWidth * (i + 1);

            mGridLines[index++] = dx;            // start X0
            mGridLines[index++] = 0;             // start Y0
            mGridLines[index++] = dx;            // stop  X1
            mGridLines[index++] = mLayoutHeight; // stop  Y1
        }

        for (int i = 0; i < mGridLineCol - 1; i++) {
            int dy = colWidth * (i + 1);

            mGridLines[index++] = 0;             // start X0
            mGridLines[index++] = dy;            // start Y0
            mGridLines[index++] = mLayoutWidht;  // stop  X1
            mGridLines[index++] = dy;            // stop  Y1                                                                         //stop  Yi
        }

        canvas.drawLines(mGridLines, mGridLinePaint);
    }

    public void save(){
        mSaveMatrix = new Matrix();
        mSaveMatrix.set(mBaseMatrix);
    }

    public void restore(){
        mBaseMatrix = new Matrix();
        mBaseMatrix.set(mSaveMatrix);
        setImageMatrix(mBaseMatrix);
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

    public void setRotation(float degress){
        mBaseMatrix.postRotate(degress, mLayoutWidht / 2, mLayoutHeight / 2);
        setImageMatrix(mBaseMatrix);

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
