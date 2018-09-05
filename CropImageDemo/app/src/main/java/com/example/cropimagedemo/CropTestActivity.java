package com.example.cropimagedemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.cropimagedemo.imagecrop.view.ImageCropView;

public class CropTestActivity extends AppCompatActivity {

    private ImageCropView ivCropView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_test);

        ivCropView = findViewById(R.id.ivCropView);
        Glide.with(this).load("https://cdn.arstechnica.net/wp-content/uploads/2018/03/Android-P-800x426.jpg").into(ivCropView);
    }

    public void OnButtonClick(View v){
        switch (v.getId()){
            case R.id.rotateBtn:
                ivCropView.setCropSquare(false);
                ivCropView.setImageRotation(-90);
                break;
            case R.id.radio32Btn:
                ivCropView.setCropSquare(false);
                ivCropView.setAspectRatio(3, 2);
                ivCropView.resetDisplay();
                ivCropView.resetMatrix();
                break;
            case R.id.radio23Btn:
                ivCropView.setCropSquare(false);
                ivCropView.setAspectRatio(2, 3);
                ivCropView.resetDisplay();
                ivCropView.resetMatrix();
                break;
            case R.id.squareBtn:
//                ivCropView.setCropSquare(true);
                ivCropView.setAspectRatio(1, 1);
                ivCropView.resetDisplay();
                ivCropView.resetMatrix();
                break;
        }
    }
}
