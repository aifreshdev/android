package com.example.cropimagedemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class CropOverlyView extends View {

    private Paint mPaintClear;
    private Paint mPaintBg;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private PorterDuffXfermode mBlender;
    private float abcHeight = 0;

    public CropOverlyView(Context context) {
        super(context);
        init(context);
    }

    public CropOverlyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){

        abcHeight = getActionBarSize(context);

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

        int margin = 50;
        float ratio = 2f / 3f;
        int height = (int) (canvasWidth * ratio);

        canvas.drawColor(Color.parseColor("#8C000000"));

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(canvasWidth - (margin * 2),
                    height + (int) abcHeight,
                    Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }

        drawOnCanvas(mCanvas, mPaintClear);
        canvas.drawBitmap(mBitmap, margin, height / 2, mPaintClear);
    }

    protected void drawOnCanvas(Canvas canvas, Paint paint) {
        canvas.drawCircle(0, 0, 100, paint);
    }

    private float getActionBarSize(Context context){
        TypedValue tv = new TypedValue();
        if(context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)){
            return TypedValue.complexToDimension(tv.data, context.getResources().getDisplayMetrics());
        }

        return 0;
    }
}
