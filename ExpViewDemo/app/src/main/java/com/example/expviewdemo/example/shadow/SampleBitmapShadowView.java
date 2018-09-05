package com.example.expviewdemo.example.shadow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.expviewdemo.R;

public class SampleBitmapShadowView extends View {

    private Bitmap mBitmap;
    private Bitmap mShadowBitmap;

    public SampleBitmapShadowView(Context context) {
        super(context);
        init();
    }

    public SampleBitmapShadowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        int width = 200;
        int height = 200;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = width;
        options.outHeight = height;

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.android_robot, options);
        mShadowBitmap = createShadowBitmap(mBitmap, width, height, Color.BLACK, 3, 3, 4);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mShadowBitmap, 0, 0, null);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    public Bitmap createShadowBitmap(Bitmap bm, int dstWidth, int dstHeight, int color, int size, float dx, float dy) {
        final Bitmap mask = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ALPHA_8);

        //
        // RectF
        //            top
        //            ||
        // ----left object ----right (width)
        //            ||
        //          bottom (height)
        //
        final Matrix scaleToFit = new Matrix();
        final RectF src = new RectF(0, 0, dstWidth, dstHeight);
        final RectF dst = new RectF(0, 0, dstWidth - dx, dstHeight - dy);
        scaleToFit.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        // scale matrix => new matrix => add dx/dy
        final Matrix dropShadow = new Matrix(scaleToFit);
        dropShadow.postTranslate(dx, dy);

        // bitmap => canvas => draw
        final Canvas maskCanvas = new Canvas(mask);
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskCanvas.drawBitmap(bm, scaleToFit, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        maskCanvas.drawBitmap(bm, dropShadow, paint);

        // BlurMaskFilter => blur bitmap => canvas draw
        final BlurMaskFilter filter = new BlurMaskFilter(size, BlurMaskFilter.Blur.NORMAL);
        // reuse paint object
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setMaskFilter(filter);
        paint.setFilterBitmap(true);

        final Bitmap ret = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        final Canvas retCanvas = new Canvas(ret);
        retCanvas.drawBitmap(mask, 0, 0, paint);
        retCanvas.drawBitmap(bm, scaleToFit, null);
        mask.recycle();

        return ret;
    }
}
