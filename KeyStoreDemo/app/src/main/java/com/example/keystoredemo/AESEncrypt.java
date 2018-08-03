package com.example.keystoredemo;

import android.content.Context;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncrypt {

    private final String TRANSFORMATION_SYMMETRIC = "AES/GCM/NoPadding";
    private final String HASH_ALGORITHM = "SHA-256";

    private final Context mContext;
    private KeyStoreWrapper mKeyStoreWrapper;
    private SecretKeySpec mSecretKeySpec;
    private byte[] mIvBytes;
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
        mKeyStoreWrapper.initWithRsa(alias);
        mSecretKeySpec = getSecretKeySpec(alias);

        String ivFile = mContext.getCacheDir() + "/iv.txt";
        //String aliasFile = mContext.getCacheDir()+"/alias.txt";
        //String aliass = readFileData(aliasFile);
        String iv = readFileData(ivFile);

//        if(aliass == null) {
//            writeFileData(aliasFile, mKeyStoreWrapper.encryptKey(alias));
//        }else{
//            mKeyStoreWrapper.decryptKey(aliass);
//        }

        if (iv != null) {
            mIvBytes = hexToBytes(mKeyStoreWrapper.decryptKey(iv));
        } else {
            mIvBytes = getRandomIv();
            writeFileData(ivFile, mKeyStoreWrapper.encryptKey(bytesToHex(mIvBytes)));
        }

        if (mIvBytes == null || mIvBytes.length == 0) {
            mIvBytes = getRandomIv();
        }

    }

    /**
     * @param filename
     * @param data     Save RSA key, don't use SharedPreference
     */
    public void saveKey(String filename, String data) {
        String path = mContext.getCacheDir() + "/" + filename;
        writeFileData(path, data);
    }

    /**
     * @param filename Save RSA key in cache file, don't use SharedPreference
     */
    public String readKey(String filename) {
        String path = mContext.getCacheDir() + "/" + filename;
        return readFileData(path);
    }

    public void writeFileData(String path, String data) {
        try {
            FileOutputStream fout = new FileOutputStream(new File(path));
            fout.write(data.getBytes());
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readFileData(String path) {
        try {
            FileInputStream fin = new FileInputStream(new File(path));
            int lenght = fin.available();
            byte[] buffer = new byte[lenght];
            fin.read(buffer);

            return new String(buffer, Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private SecretKeySpec getSecretKeySpec(String password) {
        try {
            final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] bytes = password.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            byte[] key = digest.digest();
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            return secretKeySpec;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getEncrypt(String inputStr) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION_SYMMETRIC);
            cipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, new IvParameterSpec(mIvBytes));
            byte[] bytes = cipher.doFinal(inputStr.getBytes(Charset.forName("UTF-8")));
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getDecrypt(String inputStr) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION_SYMMETRIC);
            cipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, new IvParameterSpec(mIvBytes));
            byte[] encryptedData = Base64.decode(inputStr, Base64.DEFAULT);
            byte[] decodedData = cipher.doFinal(encryptedData);
            return new String(decodedData, Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private byte[] getRandomIv() {
        SecureRandom r = new SecureRandom();
        byte[] ivBytes = new byte[16];
        r.nextBytes(ivBytes);
        return ivBytes;
    }

    private String bytesToHex(byte[] data) {
        StringBuilder builder = new StringBuilder();
        for (byte b : data) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private byte[] hexToBytes(String data) {
        if (data == null || data.length() < 2) {
            return new byte[0];
        }

        byte[] byteResult = new byte[data.length() / 2];
        for (int i = 0; i < byteResult.length - 1; i++) {
            int index = i * 2;
            String str = data.substring(index, index + 2); // e.g ab "cd" ef "gh"
            byteResult[i] = (byte) Integer.parseInt(str, 16);
        }
        return byteResult;
    }

    private String getWrapKey(Key secretKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_SYMMETRIC);
        cipher.init(Cipher.WRAP_MODE, publicKey);
        byte[] encryptKey = cipher.wrap(secretKey);
        return Base64.encodeToString(encryptKey, Base64.DEFAULT);
    }

    private Key getUnWrapKey(String wrapperKey, PrivateKey privateKey) throws Exception {
        byte[] decrptyKey = Base64.decode(wrapperKey, Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_SYMMETRIC);
        cipher.init(Cipher.UNWRAP_MODE, privateKey);
        return cipher.unwrap(decrptyKey, "AES", Cipher.SECRET_KEY);
    }
}
