package com.example.keystoredemo;

import android.content.Context;
import android.util.Base64;

import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class RSAEncrypt {

    private KeyStoreWrapper mKeyStoreWrapper;
    private static RSAEncrypt sInstance;
    private final Context mContext;

    public static synchronized RSAEncrypt getInstance(Context context){
        if(sInstance == null){
            sInstance = new RSAEncrypt(context);
        }

        return sInstance;
    }

    private RSAEncrypt(Context context){
        mContext = context;
    }

    public void init(String alias){
        mKeyStoreWrapper = KeyStoreWrapper.getInstance(mContext);
        mKeyStoreWrapper.initWithRsa(alias);
    }

    /**
     *
     * @param inputStr
     * API 19 : cipher.provider.name = AndroidOpenSSL
     * API 23, 24 : cipher.provider.name = AndroidKeyStoreBCWorkaround
     * AndroidKeyStore does not provide RSA/ECB/PKCS1Padding
     * @return
     */

    public String getEncrypt(String inputStr){
        try {
            PublicKey publicKey = mKeyStoreWrapper.getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = cipher.doFinal(inputStr.getBytes(Charset.forName("UTF-8")));
            return Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getDecrypt(String inputStr){
        try {
            PrivateKey privateKey = mKeyStoreWrapper.getPrivateKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encryptedData = Base64.decode(inputStr, Base64.DEFAULT);
            byte[] decodedData = cipher.doFinal(encryptedData);
            return new String(decodedData, Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
