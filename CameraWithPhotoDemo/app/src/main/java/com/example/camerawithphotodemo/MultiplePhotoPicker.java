package com.example.camerawithphotodemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;

public class MultiplePhotoPicker extends AppCompatActivity {

    public static void openPicker(Context context){
        Intent intent = new Intent(context, MultiplePhotoPicker.class);
    }

}
