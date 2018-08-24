package com.example.cropimagedemo.demo.example_5;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.cropimagedemo.R;

public class TouchTestActivity5 extends AppCompatActivity {

    private TouchView5 touchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_test_5);

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
        }
    }
}
