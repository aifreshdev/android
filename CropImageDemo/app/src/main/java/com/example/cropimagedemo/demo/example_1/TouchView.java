package com.example.cropimagedemo.demo.example_1;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class TouchView extends View {

    private String TAG = "TouchView";

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleDetector;

    private SimpeGestureListener mGestureListener;
    private SimpleScaleListener mScaleListener;

    public TouchView(Context context) {
        super(context);
        init();
    }

    public TouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mGestureListener = new SimpeGestureListener();
        mScaleListener = new SimpleScaleListener();

        mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
        mGestureDetector = new GestureDetector(getContext(), mGestureListener, null, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mScaleDetector != null){
            mScaleDetector.onTouchEvent(event);
        }

        if(mGestureDetector != null){
            mGestureDetector.onTouchEvent(event);
        }

        return true;
    }

    private class SimpeGestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i(TAG, "onScroll: x " + distanceX + ", y " + distanceY);
            return true;
        }
    }

    private class SimpleScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.i(TAG, "onScale: " + detector.getCurrentSpan());
            return true;
        }
    }
}
