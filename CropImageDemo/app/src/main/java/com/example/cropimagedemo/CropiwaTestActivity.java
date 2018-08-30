package com.example.cropimagedemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.steelkiwi.cropiwa.CropIwaView;

public class CropiwaTestActivity extends AppCompatActivity {

    private CropIwaView ivCropView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropiwa_test);

        ivCropView = findViewById(R.id.ivCropView);

        Glide.with(this).load("https://cdn.arstechnica.net/wp-content/uploads/2018/03/Android-P-800x426.jpg").into(ivCropView);
    }

    public void OnButtonClick(View v){
        switch (v.getId()){
            case R.id.rotateBtn:
                break;
            case R.id.resetBtn:
                break;
            case R.id.ratio32Btn:
                break;
            case R.id.ratio23Btn:
                break;
        }
    }
}
