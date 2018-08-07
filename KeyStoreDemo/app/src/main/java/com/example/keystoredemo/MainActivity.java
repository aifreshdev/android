package com.example.keystoredemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.keystoredemo.test.AESEncrypt;
import com.example.keystoredemo.utils.keystore.AlgorithmType;
import com.example.keystoredemo.utils.keystore.crypter.Encryptor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String data = "hello world!";

        String alias = "sample-alias";
        AESEncrypt.getInstance(this).init(alias);
        String encryptData = AESEncrypt.getInstance(this).getEncrypt(data);
        String decryptData = AESEncrypt.getInstance(this).getDecrypt(encryptData);

        Encryptor e = Encryptor.getInstance(this, AlgorithmType.RSA);
        e.decrypt("");
    }
}
