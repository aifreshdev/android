package com.example.expviewdemo.example.porterduff;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class SampleOveleryView extends View {

    private Paint mPaintClear;
    private Paint mPaintBg;
    private PorterDuffXfermode mBlender;

    public SampleOveleryView(Context context) {
        super(context);
        init();
    }

    public SampleOveleryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){

        mPaintBg = new Paint();
        mPaintBg.setColor(Color.BLACK);

        mPaintClear = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintClear.setAntiAlias(true);

        mBlender = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
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
        //canvas.drawRect(0, 0, canvasWidth, canvasHeight, mPaintBg);
        //int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 250, mPaintClear);

    }
}
