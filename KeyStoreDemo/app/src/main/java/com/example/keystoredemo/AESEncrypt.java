package com.example.keystoredemo;

import android.content.Context;
import android.util.Base64;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AESEncrypt {

    private final String TRANSFORMATION_SYMMETRIC = "AES/CBC/PKCS7Padding";
    private final Context mContext;
    private KeyStoreWrapper mKeyStoreWrapper;
    private static AESEncrypt sInstance;

    public static synchronized AESEncrypt getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AESEncrypt(context);
        }

        return sInstance;
    }

    public AESEncrypt(Context context) {
        mContext = context;
    }

    public void init(String alias) {
        mKeyStoreWrapper = KeyStoreWrapper.getInstance(mContext);
        mKeyStoreWrapper.initWithAes(alias);
    }

    public String getEncrypt(String inputStr) {
        try {
            SecretKey secretKey = mKeyStoreWrapper.getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION_SYMMETRIC);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(inputStr.getBytes(Charset.forName("UTF-8")));
            return Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getDecrypt(String inputStr) {
        try {
            SecretKey secretKey = mKeyStoreWrapper.getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION_SYMMETRIC);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedData = Base64.decode(inputStr, Base64.DEFAULT);
            byte[] decodedData = cipher.doFinal(encryptedData);
            return new String(decodedData, Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getWrapKey(Key secretKey, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_SYMMETRIC);
        cipher.init(Cipher.WRAP_MODE, publicKey);
        byte[] encryptKey = cipher.wrap(secretKey);
        return Base64.encodeToString(encryptKey, Base64.DEFAULT);
    }

    private Key getUnWrapKey(String wrapperKey, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException {
        byte[] decrptyKey = Base64.decode(wrapperKey, Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_SYMMETRIC);
        cipher.init(Cipher.UNWRAP_MODE, privateKey);
        return cipher.unwrap(decrptyKey, "AES", Cipher.SECRET_KEY);
    }
}
