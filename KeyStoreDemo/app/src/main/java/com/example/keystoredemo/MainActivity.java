package com.example.keystoredemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String str = "Hello World!";
//        String encryptData = Encryptor.getInstance(this).encrypt(str);
//        String decryptData = Encryptor.getInstance(this).decrypt(encryptData);

        //Encryptor.getInstance(this).delete();
        String e = Encryptor.getInstance(this).rasEncrypt(str);
        String s = Encryptor.getInstance(this).rasDecrypt(e);
    }
}
