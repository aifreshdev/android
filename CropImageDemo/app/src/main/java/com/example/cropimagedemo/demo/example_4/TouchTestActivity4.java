package com.example.cropimagedemo.demo.example_4;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.cropimagedemo.R;

public class TouchTestActivity4 extends AppCompatActivity {

    private TouchView4 touchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_test_4);

        touchView = findViewById(R.id.touchView);
    }

    public void OnButtonClick(View v){
        touchView.setRotation(-90);
    }
}
