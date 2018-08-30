package com.example.cropimagedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.cropimagedemo.demo.example_1.TouchTestActivity;
import com.example.cropimagedemo.demo.example_2.TouchTestActivity2;
import com.example.cropimagedemo.demo.example_3.TouchTestActivity3;
import com.example.cropimagedemo.demo.example_4.TouchTestActivity4;
import com.example.cropimagedemo.demo.example_5.TouchTestActivity5;
import com.example.cropimagedemo.demo.example_6.TouchTestActivity6;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OnButtonClick(View v){
        switch (v.getId()){
            case R.id.touchTestBtn:
                startActivity(new Intent(this, TouchTestActivity.class));
                break;
            case R.id.touchTestBtn2:
                startActivity(new Intent(this, TouchTestActivity2.class));
                break;
            case R.id.touchTestBtn3:
                startActivity(new Intent(this, TouchTestActivity3.class));
                break;
            case R.id.touchTestBtn4:
                startActivity(new Intent(this, TouchTestActivity4.class));
                break;
            case R.id.touchTestBtn5:
                startActivity(new Intent(this, TouchTestActivity5.class));
                break;
            case R.id.touchTestBtn6:
                startActivity(new Intent(this, TouchTestActivity6.class));
                break;
            case R.id.cropTestBtn1:
                startActivity(new Intent(this, CropTestActivity.class));
                break;
            case R.id.cropTestBtn2:
                startActivity(new Intent(this, CropiwaTestActivity.class));
                break;
        }
    }


}
