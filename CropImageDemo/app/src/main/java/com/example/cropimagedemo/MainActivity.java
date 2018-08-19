package com.example.cropimagedemo;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.cropimagedemo.view.CropView;
import com.example.cropimagedemo.view.TouchImageView;

public class MainActivity extends AppCompatActivity {

    private TouchImageView mTouchImageView;
    private CropView mCropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCropView = findViewById(R.id.ivCropView);

//        mTouchImageView = findViewById(R.id.ivPhoto);
        Glide.with(this).load("https://cdn.arstechnica.net/wp-content/uploads/2018/03/Android-P-800x426.jpg")
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        mCropView.setImageDrawable(resource);
                        //mTouchImageView.setImageDrawable(resource);
                    }
                });
    }
}
