package com.example.keystoredemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RSAEncrypt.getInstance(this).clear().init("sample-alias");

        String str = "Hello World!";
        RSAEncrypt.getInstance(this).init("sample-alias");
        String enStr = RSAEncrypt.getInstance(this).getEncrypt(str);
        String deStr = RSAEncrypt.getInstance(this).getDecrypt(enStr);

//        String encryptData = TestEncryptor.getInstance(this).encrypt(str);
//        String decryptData = TestEncryptor.getInstance(this).decrypt(encryptData);

        //TestEncryptor.getInstance(this).delete();
        //String e = TestEncryptor.getInstance(this).rasEncrypt(str);
        //String s = TestEncryptor.getInstance(this).rasDecrypt(e);
    }
}
