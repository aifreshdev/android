package com.example.camerawithphotodemo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.camerawithphotodemo.imagepicker.MultiplePhotoPicker;

public class MainActivity extends AppCompatActivity {

    /**
     * https://github.com/naver/android-imagecropview
     */
    private final String TAG = "MainActivity";
    private final int REQUEST_CODE_PHOTO_MULTIPLE_PICKER = 1;
    private Button imgPickerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgPickerBtn = findViewById(R.id.imgPickerBtn);
        imgPickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiplePhotoPicker.open(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: " + requestCode);
    }

    private void openDefaultMultiplePicker(){
        Intent pickerIntent = new Intent();
        pickerIntent.setType("image/*");
        pickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickerIntent,"Select Picture"), REQUEST_CODE_PHOTO_MULTIPLE_PICKER);
    }
}
