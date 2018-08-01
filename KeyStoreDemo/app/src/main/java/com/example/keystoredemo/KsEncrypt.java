package com.example.keystoredemo;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.util.Calendar;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

public class KsEncrypt {

    private final String TAG = "KsEncrypt";
    // provider : AndroidKeyStore, AndroidOpenSSL, AndroidKeyStoreBCWorkaround
    private final String KS_PROVIDER = "AndroidKeyStore";
    private final KeyStore mKeyStore = getKeyStore();
    private static KsEncrypt sInstance;
    private Context mContext;
    private String mAlias = "sample-alias";

    public static synchronized KsEncrypt getInstance(Context context){
        if(sInstance == null){
            sInstance = new KsEncrypt(context);
        }

        return sInstance;
    }

    private KsEncrypt(Context context){
        mContext = context;
    }

    public void init(String alias){
        setAlias(alias);
        keyCreator(alias);
    }

    public KsEncrypt clear() {
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
            PublicKey publicKey = getPublicKey(mAlias);
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
            PrivateKey privateKey = getPrivateKey(mAlias);
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

    private void keyCreator(String alias) {
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

    private PublicKey getPublicKey(String alias) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        return getEntry(alias).getCertificate().getPublicKey();
    }

    private PrivateKey getPrivateKey(String alias) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        return getEntry(alias).getPrivateKey();
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
