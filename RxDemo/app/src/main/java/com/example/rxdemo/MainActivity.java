package com.example.rxdemo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.example.rxdemo.rxmoduel.ObservableEmitterSample;
import com.example.rxdemo.rxmoduel.ObservableFlatMapSample;
import com.example.rxdemo.rxmoduel.ObservableMapSample;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private AppCompatSpinner mSpRxType;
    private String[] imgUrls = {
            "https://portaltele.com.ua/wp-content/uploads/2018/07/Xiaomi-Mi-A1-Android-P-52.jpg",
            "https://cdn3.techadvisor.co.uk/cmsdata/features/3670552/android_popsicle_1600_thumb800.png",
            "https://cdn.arstechnica.net/wp-content/uploads/2018/03/Android-P-800x426.jpg",
            "https://www.xda-developers.com/files/2018/03/Android-P.png",
            "https://cdn.arstechnica.net/wp-content/uploads/2018/04/Google-IO-2018-800x420.png"
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpRxType = findViewById(R.id.spRxType);
        mSpRxType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        ObservableEmitterSample.executeEmitter();
                        break;
                    case 1:
                        ObservableEmitterSample.executeBitmapEmiter(MainActivity.this);
                        break;
                    case 2:
                        ObservableEmitterSample.executeDisposableEmitter();
                        break;
                    case 3:
                        ObservableFlatMapSample.sInstance.executeFlatMap();
                        break;
                    case 3:
                        ObservableMapSample.sInstance.executeMap().subscribe(ObservableMapSample.sInstance.getObserver());
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObservableFlatMapSample.sInstance.dispose();
    }
}
