package com.example.keystoredemo.utils.keystore;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.util.Calendar;
import java.util.Enumeration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

public abstract class KeyStoreWrapper {

    /**
     * https://proandroiddev.com/secure-data-in-android-encryption-in-android-part-2-991a89e55a23
     * https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec
     * https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
     */
    private final String TAG = "KeyStoreWrapper";
    private final String HASH_ALGORITHM = "SHA-256";
    private final String KS_PROVIDER = "AndroidKeyStore";
    private final KeyStore mKeyStore = getKeyStore();

    public void remove(String alias) {
        try {
            mKeyStore.deleteEntry(alias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void log() {
        try {
            Enumeration<String> aliases = mKeyStore.aliases();
            while (aliases.hasMoreElements()) {
                Log.i(TAG, "Key " + aliases.nextElement());
            }
        } catch (Exception ignored) {
        }
    }

    private KeyStore getKeyStore() {
        try {
            // getInstance(“type”, “provider”)
            KeyStore keyStore = KeyStore.getInstance(KS_PROVIDER);
            keyStore.load(null);
            Log.i(TAG, "Algorithm - Type : " + keyStore.getType() + ", Provider : " + keyStore.getProvider());
            return keyStore;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public SecretKey getSecretKey() {
        try {
            return (SecretKey) mKeyStore.getKey(getAlias(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private SecretKeySpec getSecretKeySpec(String alias) {
        try {
            final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] bytes = alias.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            byte[] key = digest.digest();
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            return secretKeySpec;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private SecretKey createSecretKey() {
        try {
            KeyGenerator kpGenerator;
            // set ANDROID_KEY_STORE & alias into keystore
            kpGenerator = KeyGenerator.getInstance("AES", KS_PROVIDER);
            kpGenerator.init(new KeyGenParameterSpec.Builder(getAlias(),
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(getBlockMode())
                    .setEncryptionPaddings(getEncryptionPadding()).build());
            return kpGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void createAsymmetricKey(Context context) {
        // getInstance(“algorithm”, “provider”)
        if (!isSigningKey(getAlias())) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 20);
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", KS_PROVIDER);
                if (hasMarshmallow()) {
                    keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(getAlias(),
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(getBlockMode())
                            .setEncryptionPaddings(getEncryptionPadding())
                            .setCertificateSerialNumber(BigInteger.ONE)
                            .setCertificateSubject(new X500Principal(String.format("CN=%s CA Certificate", getAlias())))
                            .setCertificateNotBefore(start.getTime())
                            .setCertificateNotAfter(end.getTime()).build());
                } else {
                    keyPairGenerator.initialize(new KeyPairGeneratorSpec.Builder(context)
                            .setAlias(getAlias())
                            .setSerialNumber(BigInteger.ONE)
                            .setSubject(new X500Principal(String.format("CN=%s CA Certificate", getAlias())))
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
        return getEntry(getAlias()).getCertificate().getPublicKey();
    }

    public PrivateKey getPrivateKey() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        return getEntry(getAlias()).getPrivateKey();
    }

    private KeyStore.PrivateKeyEntry getEntry(String alias) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        if (alias != null) {
            KeyStore.Entry entry = mKeyStore.getEntry(alias, null);
            if (entry instanceof KeyStore.PrivateKeyEntry) {
                return (KeyStore.PrivateKeyEntry) entry;
            }
        }

        return null;
    }

    private boolean isSigningKey(String alias) {
        if (alias != null) {
            try {
                KeyStore.Entry entry = getEntry(alias);
                return mKeyStore.containsAlias(alias) && entry != null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
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

    public boolean hasMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public abstract String getAlias();
    public abstract String getAlgorithmType();
    public abstract String getBlockMode();
    public abstract String getEncryptionPadding();

}
