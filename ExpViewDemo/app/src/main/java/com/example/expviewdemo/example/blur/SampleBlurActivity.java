package com.example.expviewdemo.example.blur;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.expviewdemo.R;

import java.io.ByteArrayOutputStream;

public class SampleBlurActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout container;
    private Button blurBtn;
    private ImageView ivOriginal, ivBlur;
    private Bitmap cacheBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_blur);

        container = findViewById(R.id.container);
        ivOriginal = findViewById(R.id.ivOriginal);
        ivBlur = findViewById(R.id.ivBlur);
        blurBtn = findViewById(R.id.blurBtn);
        blurBtn.setOnClickListener(this);

        ivOriginal.setDrawingCacheEnabled(true);

        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Matrix matrix = ivOriginal.getImageMatrix();
                //matrix.setScale(10, 10, ivOriginal.getWidth() / 2, ivOriginal.getHeight() / 2);
                //ivOriginal.setImageMatrix(matrix);
                cacheBitmap = ivOriginal.getDrawingCache();

                container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    @Override
    public void onClick(View view) {
        if(cacheBitmap != null){
            new BlurTask(this).execute(cacheBitmap);
        }
    }

    private class BlurTask extends AsyncTask<Bitmap, Void, Bitmap>{

        private Context context;
        private ProgressDialog progressDialog;
        private RenderScript renderScript;
        private ScriptIntrinsicBlur blurScript;
        private Allocation blurOut;
        private Allocation blurIn;
        private float scale = 0.05f;

        public BlurTask(Context context){
            this.context = context;
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setMessage("Loading...");
        }

        @Override
        protected void onPreExecute() {
            if(progressDialog != null && !progressDialog.isShowing()){
                progressDialog.show();
            }
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap smallBitmap = getSmallBitmap(bitmaps[0]);
            return blur(smallBitmap, 20);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(progressDialog != null){
                progressDialog.dismiss();
            }

            ivBlur.setImageBitmap(bitmap);
        }

        /**
         * Radius out of range (0 < r <= 25)
         * RenderScript only work on Config.ARGB_8888)
         */
        private Bitmap blur(Bitmap fromBitmap, float blurRadius) {
            int width = Math.round(fromBitmap.getWidth() * scale);
            int height = Math.round(fromBitmap.getHeight() * scale);

            Bitmap bitmap = Bitmap.createScaledBitmap(fromBitmap, width, height, false);

            /**
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            Paint paint = new Paint();
//            paint.setFlags(Paint.FILTER_BITMAP_FLAG);
              //Make it frosty
//            paint.setXfermode(
//                    new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//            ColorFilter filter =
//                    new LightingColorFilter(0xFFFFFFFF, 0x00222222); // lighten
//            //ColorFilter filter =
//            //   new LightingColorFilter(0xFF7F7F7F, 0x00000000); // darken
//            paint.setColorFilter(filter);

            //build drawing destination boundaries
            //final RectF destRect = new RectF(0, 0, width, height);
            //final Rect srcRect = new Rect(0,0, width, height);
            //canvas.drawBitmap(fromBitmap, srcRect, destRect, paint);
             **/

            Bitmap toBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            renderScript = RenderScript.create(context);
            blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

            blurIn = Allocation.createFromBitmap(renderScript, bitmap,
                    Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            blurOut = Allocation.createTyped(renderScript, blurIn.getType());

            blurScript.setRadius(blurRadius);
            blurIn.copyFrom(bitmap);
            blurScript.setInput(blurIn);
            blurScript.forEach(blurOut);
            blurOut.copyTo(toBitmap);
            blurIn.destroy();
            bitmap.recycle();

            return toBitmap;
        }

        private Bitmap getSmallBitmap(Bitmap bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 1, baos);
            return bitmap;
        }

        private Bitmap convertRGB565toARGB888(Bitmap bitmap) {
            return bitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
    }

}
