package com.example.keystoredemo;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;
import java.util.Enumeration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

public class KeyStoreWrapper {

    /**
     * https://proandroiddev.com/secure-data-in-android-encryption-in-android-part-2-991a89e55a23
     * https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec
     * https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
     */
    private final String TAG = "KeyStoreWrapper";
    private final String KS_PROVIDER = "AndroidKeyStore";
    private final String HASH_ALGORITHM = "SHA-256";
    private final KeyStore mKeyStore = getKeyStore();
    private final Context mContext;

    private String mAlias = "sample-alias";
    private static KeyStoreWrapper sInstance;

    public static synchronized KeyStoreWrapper getInstance(Context context){
        if(sInstance == null){
            sInstance = new KeyStoreWrapper(context);
        }

        return sInstance;
    }

    private KeyStoreWrapper(Context context){
        mContext = context;
    }

    public void initWithRsa(String alias){
        setAlias(alias);
        rsaKeyCreator(alias);
    }

    public void initWithAes(String alias){
        setAlias(alias);
        //genSecretKey();
    }

    public KeyStoreWrapper clear() {
        try {
            mKeyStore.deleteEntry(mAlias);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public void log(){
        try {
            Enumeration<String> aliases = mKeyStore.aliases();
            while (aliases.hasMoreElements()) {
                Log.i(TAG, "Key " + aliases.nextElement());
            }
        }catch (Exception ignored){}
    }

    public void setAlias(String alias){
        mAlias = alias;
    }

    private KeyStore getKeyStore() {
        try {
            // getInstance(“type”, “provider”)
            KeyStore keyStore = KeyStore.getInstance(KS_PROVIDER);
            keyStore.load(null);
            Log.i(TAG, "Type : " + keyStore.getType() + ", Provider : " + keyStore.getProvider());
            return keyStore;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

//    public SecretKey getSecretKey(){
//        try{
//            return (SecretKey) mKeyStore.getKey(mAlias, null);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    public SecretKey getSecretKeyWithJ(){
//        try{
//            return KeyGenerator.getInstance("AES", "BC").generateKey();
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    public SecretKeySpec getSecretKeySpec() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] bytes = mAlias.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }


    public SecretKey getSecretKey() {
        try {
            KeyGenerator kpGenerator;
            if (hasMarshmallow()) {
                // set ANDROID_KEY_STORE & alias into keystore
                kpGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KS_PROVIDER);
                kpGenerator.init(new KeyGenParameterSpec.Builder(mAlias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build());

            }else{
                kpGenerator = KeyGenerator.getInstance("AES");
            }

            return kpGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void rsaKeyCreator(String alias) {
        // getInstance(“algorithm”, “provider”)
        if(!isSigningKey(alias)) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 20);
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", KS_PROVIDER);
                if (hasMarshmallow()) {
                    keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(alias,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .setCertificateSerialNumber(BigInteger.ONE)
                            .setCertificateSubject(new X500Principal(String.format("CN=%s CA Certificate", alias)))
                            .setCertificateNotBefore(start.getTime())
                            .setCertificateNotAfter(end.getTime()).build());
                } else {
                    keyPairGenerator.initialize(new KeyPairGeneratorSpec.Builder(mContext)
                            .setAlias(alias)
                            .setSerialNumber(BigInteger.ONE)
                            .setSubject(new X500Principal(String.format("CN=%s CA Certificate", alias)))
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime()).build());
                }

                keyPairGenerator.generateKeyPair();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public PublicKey getPublicKey() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        return getEntry(mAlias).getCertificate().getPublicKey();
    }

    public PrivateKey getPrivateKey() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        return getEntry(mAlias).getPrivateKey();
    }

    private KeyStore.PrivateKeyEntry getEntry(String alias) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        if(alias != null) {
            KeyStore.Entry entry = mKeyStore.getEntry(alias, null);
            if (entry instanceof KeyStore.PrivateKeyEntry) {
                return (KeyStore.PrivateKeyEntry) entry;
            }
        }

        return null;
    }

    private boolean isSigningKey(String alias) {
        if(alias != null) {
            try {
                KeyStore.Entry entry = getEntry(alias);
                return !mKeyStore.containsAlias(alias) && entry != null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private boolean hasMarshmallow(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
