package com.example.keystoredemo;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.security.auth.x500.X500Principal;

import static java.security.spec.RSAKeyGenParameterSpec.F4;

public class TestEncryptor {

    private Context mContext;
    private String TAG = "TestEncryptor";
    private String mAlias = "sample-alias";

    private final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private static TestEncryptor sInstance;
    private byte[] mIV;

    /**
     * Cipher.getInstance(type/block/paddingtype e.g : RSA/ECB/PKCS1Padding)
     */
    public synchronized static TestEncryptor getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TestEncryptor(context);
        }

        return sInstance;
    }

    private TestEncryptor(Context context) {
        mContext = context;
        if(!isSigningKey(mAlias)) {
            createCertificate();
        }
    }

    private SecretKey genAESSecretKey() {
        try {
            KeyGenerator kpGenerator;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // set ANDROID_KEY_STORE & alias into keystore
                kpGenerator = KeyGenerator.getInstance("AES", ANDROID_KEY_STORE);
                kpGenerator.init(new KeyGenParameterSpec.Builder(mAlias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
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

    private SecretKey getAESSecretKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(mAlias, null);
            return secretKeyEntry.getSecretKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String encrypt(String inputStr) {
        try {
            SecretKey key = genAESSecretKey();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            mIV = cipher.getIV();

            byte[] bytes = cipher.doFinal(inputStr.getBytes());
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String inputStr) {
        try {
            SecretKey key = getAESSecretKey();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, mIV));

            byte[] encryptedData = Base64.decode(inputStr, Base64.DEFAULT);
            byte[] decodedData = cipher.doFinal(encryptedData);
            return new String(decodedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createCertificate() {
        try {

            KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE);
            Calendar start = new GregorianCalendar();
            Calendar end = new GregorianCalendar();
            end.add(Calendar.YEAR, 20);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

                // Below Android M, use the KeyPairGeneratorSpec.Builder.
                kpGenerator.initialize(new KeyPairGeneratorSpec.Builder(mContext)
                        // You'll use the alias later to retrieve the key. It's a key
                        // for the key!
                        .setAlias(mAlias)
                        .setSubject(new X500Principal(String.format("CN=%s, O=%s", mAlias, mContext.getPackageName())))
                        .setSerialNumber(BigInteger.valueOf(Math.abs(mAlias.hashCode())))
                        // Date range of validity for the generated pair.
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build());

            } else {
                kpGenerator.initialize(new KeyGenParameterSpec.Builder(mAlias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setCertificateSubject(new X500Principal(String.format("CN=%s, O=%s", mAlias, mContext.getPackageName())))
                        .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(1024, F4))
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                        .setDigests(KeyProperties.DIGEST_SHA256,
                                KeyProperties.DIGEST_SHA384,
                                KeyProperties.DIGEST_SHA512)
                        .setCertificateSerialNumber(BigInteger.valueOf(Math.abs(mAlias.hashCode())))
                        .setCertificateNotBefore(start.getTime())
                        .setCertificateNotAfter(end.getTime())
                        .build());
            }

            KeyPair kp = kpGenerator.generateKeyPair();
            // END_INCLUDE(create_spec)
            Log.d(TAG, "Public Key is: " + kp.getPublic().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String rasEncrypt(String inputStr) {
        try {

            PublicKey publicKey = getPrivateKeyEntry(mAlias).getCertificate().getPublicKey();
            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inCipher);
            cipherOutputStream.write(inputStr.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte[] vals = outputStream.toByteArray();
            String decodeData = Base64.encodeToString(vals, Base64.DEFAULT);
            return decodeData;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String rasDecrypt(String inputStr) {
        try {

            PrivateKey privateKey = getPrivateKeyEntry(mAlias).getPrivateKey();

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKey);

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(inputStr, Base64.DEFAULT)), output);

            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            String finalText = new String(bytes, 0, bytes.length, "UTF-8");
            return finalText;
        } catch (Exception ignored) {
        }

        return null;
    }

    public boolean isSigningKey(String alias) {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(alias, null);

            return keyStore.containsAlias(alias) && entry != null && (entry instanceof KeyStore.PrivateKeyEntry);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public String getSigningKey(String alias) throws CertificateEncodingException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Certificate cert = getPrivateKeyEntry(alias).getCertificate();
            if (cert == null) {
                return null;
            }
            return Base64.encodeToString(cert.getEncoded(), Base64.NO_WRAP);
        } else {
            return null;
        }
    }

    private KeyStore.PrivateKeyEntry getPrivateKeyEntry(String alias) {
        try {
            KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(alias, null);

            if (entry == null) {
                Log.w(TAG, "No key found under alias: " + alias);
                Log.w(TAG, "Exiting signData()...");
                return null;
            }

            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                Log.w(TAG, "Not an instance of a PrivateKeyEntry");
                Log.w(TAG, "Exiting signData()...");
                return null;
            }
            return (KeyStore.PrivateKeyEntry) entry;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    private void logs() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                Log.i(TAG, "Key " + aliases.nextElement());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            keyStore.deleteEntry(mAlias);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logs();
    }


}
