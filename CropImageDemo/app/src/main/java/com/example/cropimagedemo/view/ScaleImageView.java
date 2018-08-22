package com.example.cropimagedemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ScaleImageView extends AppCompatImageView {

    private float[] mMatrixValues = new float[9];
    private Matrix mBaseMatrix = new Matrix();

    private ScaleListener mScaleListener;
    private GestureListener mGestureListener;

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleDetector;

    public ScaleImageView(Context context) {
        super(context);
        init();
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setScaleType(ImageView.ScaleType.MATRIX);

        //mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
        //mGestureDetector = new GestureDetector(getContext(), mGestureListener, null, true);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {

        if(drawable == null){
            return;
        }

        mBaseMatrix = getImageMatrix();
        float formerTransX = getMatrixValue(mBaseMatrix, Matrix.MTRANS_X);
        float formerTransY = getMatrixValue(mBaseMatrix, Matrix.MTRANS_Y);

        RectF drawableRect = new RectF(0, 0, (float) drawable.getIntrinsicWidth(), (float) drawable.getIntrinsicHeight());
        RectF viewRect = new RectF(0, 0, getWidth(), getHeight());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        if (!mScaleDetector.isInProgress()) {
            mGestureDetector.onTouchEvent(event);
        }


        return true;
    }

    private float getMatrixValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    private float getImageScale(Matrix matrix) {
        return getMatrixValue(matrix, Matrix.MSCALE_X);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// ScaleListener ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float span = detector.getCurrentSpan() - detector.getPreviousSpan();
            //float targetScale = getImageScale() * detector.getScaleFactor();

            return super.onScale(detector);
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// GestureListener ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
