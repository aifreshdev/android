package com.example.expviewdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.expviewdemo.example.blur.SampleBlurActivity;
import com.example.expviewdemo.example.porterduff.SamplePorterDuffActivity;
import com.example.expviewdemo.example.shadow.SampleBitmapShadowView;
import com.example.expviewdemo.example.shadow.SampleShadowActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.samplePdxfmBtn:
                startActivity(new Intent(this, SamplePorterDuffActivity.class));
                break;
            case R.id.sampleShadowBtn:
                startActivity(new Intent(this, SampleShadowActivity.class));
                break;
            case R.id.sampleBlurBtn:
                startActivity(new Intent(this, SampleBlurActivity.class));
                break;
        }
    }
}
