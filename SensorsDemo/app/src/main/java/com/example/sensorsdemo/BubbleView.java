package com.example.sensorsdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static android.content.Context.SENSOR_SERVICE;

public class BubbleView extends View implements SensorEventListener {

    private Bitmap mBallBitmap;
    private int px, py, xVel, yVel, xMax = 100, yMax = 100;
    private float xAccel, yAccel;
    private SensorManager sensorManager;

    public BubbleView(Context context) {
        super(context);
        init();
    }

    public BubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        Bitmap src = BitmapFactory.decodeResource(getResources(), R.mipmap.google_earth);
        final int dstWidth = 100;
        final int dstHeight = 100;

        px = dstWidth / 2;
        py = dstHeight / 2;

        mBallBitmap = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, true);

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager = (SensorManager) getContext().getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this , sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBallBitmap, px, py, null);
        invalidate();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAccel = sensorEvent.values[0];
            yAccel = -sensorEvent.values[1];
            updateBall();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void updateBall() {
        float frameTime = 0.666f;
        xVel += (xAccel * frameTime);
        yVel += (yAccel * frameTime);

        float xS = (xVel / 2) * frameTime;
        float yS = (yVel / 2) * frameTime;

        px -= xS;
        py -= yS;

        if (px > xMax) {
            px = xMax;
        } else if (px < 0) {
            px = 0;
        }

        if (py > yMax) {
            py = yMax;
        } else if (py < 0) {
            py = 0;
        }

        invalidate();
    }
}
