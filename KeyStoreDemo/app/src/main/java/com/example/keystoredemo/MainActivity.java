package com.example.keystoredemo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;

import com.kazakago.cryptore.CipherAlgorithm;
import com.kazakago.cryptore.Cryptore;
import com.kazakago.cryptore.DecryptResult;
import com.kazakago.cryptore.EncryptResult;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //KeyStoreWrapper.getInstance(this).clear().initWithRsa("sample-alias");

        String str = "Hello World!";
        try {
            String e = encrypt(str);
            String d = decrypt(e);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
//
////        String str = "hello world!";
//        AESEncrypt.getInstance(this).init("sample-alias");
//        String enStr = AESEncrypt.getInstance(this).getEncrypt(str);
//        String deStr = AESEncrypt.getInstance(this).getDecrypt(enStr);
//
//        String enStr1 = TestEncryptor.getInstance(this).encrypt(str);
//        String deStr1 = TestEncryptor.getInstance(this).decrypt(enStr1);
    }

    Cryptore getCryptore() throws Exception {
        Cryptore.Builder builder = new Cryptore.Builder("sample-alias", CipherAlgorithm.AES);
        builder.setContext(this);
        return builder.build();
    }

    String encrypt(String plainStr) throws Exception {
        byte[] plainByte = plainStr.getBytes();
        EncryptResult result = getCryptore().encrypt(plainByte);
        saveCipherIV(result.getCipherIV());
        return Base64.encodeToString(result.getBytes(), Base64.DEFAULT);
    }

    String decrypt(String encryptedStr) throws Exception {
        byte[] encryptedByte = Base64.decode(encryptedStr, Base64.DEFAULT);
        byte[] cipherIV = loadCipherIV();
        DecryptResult result = getCryptore().decrypt(encryptedByte, cipherIV);
        return new String(result.getBytes());
    }

    private void saveCipherIV(byte[] cipherIV) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("_cipher_iv", Base64.encodeToString(cipherIV, Base64.DEFAULT));
        editor.apply();
    }

    private byte[] loadCipherIV() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cipherIVStr = preferences.getString("_cipher_iv", null);
        return (cipherIVStr != null) ? Base64.decode(cipherIVStr, Base64.DEFAULT) : null;
    }

}
