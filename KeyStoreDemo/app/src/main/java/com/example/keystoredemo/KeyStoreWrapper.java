package com.example.keystoredemo;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
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

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
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
    private final KeyStore mKeyStore = getKeyStore();
    private final Context mContext;

    private String mAlias = "secret";
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
        createKeys(alias);
    }

    public void initWithAes(String alias){
        setAlias(alias);
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

    public SecretKey getSecretKey(){
        try{
            return (SecretKey) mKeyStore.getKey(mAlias, null);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    /**
     *
     * @param inputStr
     * API 19 : cipher.provider.name = AndroidOpenSSL
     * API 23, 24 : cipher.provider.name = AndroidKeyStoreBCWorkaround
     * AndroidKeyStore does not provide RSA/ECB/PKCS1Padding
     * RSA keys sizes are: 512, 768, 1024, 2048, 3072, 4096
     * Ref : https://developer.android.com/training/articles/keystore#SupportedKeyPairGenerators
     * @return
     */

    public String encryptKey(String inputStr){
        try {
            PublicKey publicKey = getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = cipher.doFinal(inputStr.getBytes(Charset.forName("UTF-8")));
            return Base64.encodeToString(bytes, Base64.NO_WRAP | Base64.NO_PADDING);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String decryptKey(String inputStr){
        try {
            PrivateKey privateKey = getPrivateKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encryptedData = Base64.decode(inputStr, Base64.NO_WRAP | Base64.NO_PADDING);
            byte[] decodedData = cipher.doFinal(encryptedData);
            return new String(decodedData, Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private SecretKey createSecretKey() {
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

    private void createKeys(String alias) {
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
                return mKeyStore.containsAlias(alias) && entry != null;
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
