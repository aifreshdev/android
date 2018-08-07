package com.example.expviewdemo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class SampleBitmapOverlayView extends View {

    private Paint mPaintClear;
    private Paint mPaintBg;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private PorterDuffXfermode mBlender;

    public SampleBitmapOverlayView(Context context) {
        super(context);
        init();
    }

    public SampleBitmapOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){

        mPaintBg = new Paint();
        mPaintBg.setColor(Color.parseColor("#8C000000"));

        mBlender = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mPaintClear = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintClear.setColor(Color.RED);
        mPaintClear.setAlpha(0);
        mPaintClear.setXfermode(mBlender);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawColor(Color.parseColor("#8C000000"));

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(200,
                    200,
                    Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }

        drawOnCanvas(mCanvas, mPaintClear);
        canvas.drawBitmap(mBitmap, 0, 0, mPaintClear);
    }

    protected void drawOnCanvas(Canvas canvas, Paint paint) {
        canvas.drawCircle(0, 0, 100, paint);
    }
}
