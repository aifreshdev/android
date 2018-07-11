package com.example.notificationdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        describe(TAG, intent);
    }

    void describe(String tag, Bundle bundle) {
        Log.i(tag, "[Bundle] ==== START ====");
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.i(tag, "[" + key + " = " + bundle.get(key) + "]");
            }
        }
        Log.i(tag, "[Bundle] ==== END ====");
    }

    void describe(String tag, Intent intent) {
        Log.i(tag, "[Intent] ==== START ====");
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                describe(tag, bundle);
            }
        } else {
            Log.i(tag, "Intent is empty.");
        }
        Log.i(tag, "[Intent] ==== END ====");
    }

}
