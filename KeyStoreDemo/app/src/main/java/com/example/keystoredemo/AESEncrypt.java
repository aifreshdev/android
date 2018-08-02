package com.example.keystoredemo;

import android.content.Context;
import android.os.Build;
import android.util.Base64;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncrypt {

    private final int TAG_LENGTH_BYTES = 16;
    private final String TRANSFORMATION_SYMMETRIC = "AES/CBC/NoPadding";
    private final Context mContext;
    private KeyStoreWrapper mKeyStoreWrapper;
    private static AESEncrypt sInstance;
    private byte[] mIvSpec;

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

            mIvSpec = cipher.getIV();

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
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, mIvSpec));
            byte[] encryptedData = Base64.decode(inputStr, Base64.DEFAULT);
            byte[] decodedData = cipher.doFinal(encryptedData);
            return new String(decodedData, Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private AlgorithmParameterSpec getAlgorithmParams(final byte[] buf, int offset, int len) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new GCMParameterSpec(TAG_LENGTH_BYTES * 8, buf, offset, len);
        }

        // GCMParameterSpec should always be present in Java 7 or newer, but it's missing on
        // some Android devices with API level <= 19. Fortunately, we can initialize the cipher
        // with just an IvParameterSpec. It will use a tag size of 128 bits.
        return new IvParameterSpec(buf, offset, len);
    }

    private byte[] getRandomIv(){
        SecureRandom r = new SecureRandom();
        byte[] ivBytes = new byte[16];
        r.nextBytes(ivBytes);
        return ivBytes;
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
