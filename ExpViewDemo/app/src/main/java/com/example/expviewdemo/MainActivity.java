package com.example.expviewdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button samplePdxfmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        samplePdxfmBtn = findViewById(R.id.samplePdxfmBtn);
        samplePdxfmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.samplePdxfmBtn:
                startActivity(new Intent(this, SamplePorterDuffActivity.class));
                break;
        }
    }
}
