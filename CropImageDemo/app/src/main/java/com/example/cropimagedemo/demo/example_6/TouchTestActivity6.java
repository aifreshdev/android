package com.example.cropimagedemo.demo.example_6;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.cropimagedemo.R;

public class TouchTestActivity6 extends AppCompatActivity {

    private TouchView6 touchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_test_6);

        touchView = findViewById(R.id.touchView);
    }

    public void OnButtonClick(View v){
        switch (v.getId()){
            case R.id.rotateBtn:
                touchView.setRotation(-90);
                break;
            case R.id.saveBtn:
                touchView.save();
                break;
            case R.id.restoreBtn:
                touchView.restore();
                break;
            case R.id.ratioBtn1:
                touchView.setRatio(3, 2);
                touchView.setMargin(10, 10, 10, 10);
                break;
            case R.id.ratioBtn2:
                touchView.setRatio(2, 3);
                touchView.setMargin(10, 10, 10, 10);
                break;
        }
    }
}
