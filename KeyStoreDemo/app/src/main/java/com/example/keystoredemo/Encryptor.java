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
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

public class Encryptor {

    private Context mContext;
    private String TAG = "Encryptor";
    private String mAlias = "sample-alias";

    private final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    private final String TYPE_RSA = "RSA";

    private static Encryptor sInstance;
    private KeyStore mKeyStore;

    public synchronized static Encryptor getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Encryptor(context);
        }

        return sInstance;
    }

    private Encryptor(Context context) {
        mContext = context;

        try {
            mKeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            mKeyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private KeyPair createNewKey() {
        try {
            Calendar start = new GregorianCalendar();
            Calendar end = new GregorianCalendar();
            end.add(Calendar.YEAR, 1);

            KeyPairGenerator kpGenerator = KeyPairGenerator
                    .getInstance(TYPE_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

            AlgorithmParameterSpec spec;
            if (!mKeyStore.containsAlias(mAlias)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    // Below Android M, use the KeyPairGeneratorSpec.Builder.

                    spec = new KeyPairGeneratorSpec.Builder(mContext)
                            // You'll use the alias later to retrieve the key.  It's a key for the key!
                            .setAlias(mAlias)
                            // The subject used for the self-signed certificate of the generated pair
                            .setSubject(new X500Principal("CN=" + mAlias))
                            // The serial number used for the self-signed certificate of the
                            // generated pair.
                            .setSerialNumber(BigInteger.valueOf(1337))
                            // Date range of validity for the generated pair.
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();


                } else {
                    // On Android M or above, use the KeyGenparameterSpec.Builder and specify permitted
                    // properties  and restrictions of the key.
                    spec = new KeyGenParameterSpec.Builder(mAlias, KeyProperties.PURPOSE_SIGN)
                            .setCertificateSubject(new X500Principal("CN=" + mAlias))
                            .setDigests(KeyProperties.DIGEST_SHA256)
                            .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                            .setCertificateSerialNumber(BigInteger.valueOf(1337))
                            .setCertificateNotBefore(start.getTime())
                            .setCertificateNotAfter(end.getTime())
                            .build();
                }

                kpGenerator.initialize(spec);
                KeyPair kp = kpGenerator.generateKeyPair();
                // END_INCLUDE(create_spec)
                Log.d(TAG, "Public Key is: " + kp.getPublic().toString());
                logs();

                return kp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete() {
        try {
            mKeyStore.deleteEntry(mAlias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        logs();
    }

    private SecretKey getKeyStoreSecretKey(String alias) {
        try {
            return (SecretKey) mKeyStore.getKey(alias, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private SecretKey getKeyStoreSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            return keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String encrypt(String inputStr) {
        try {
            SecretKey key = getKeyStoreSecretKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] bytes = cipher.doFinal(inputStr.getBytes());
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String inputStr) {
        try {
            SecretKey key = getKeyStoreSecretKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedData = Base64.decode(inputStr, Base64.DEFAULT);
            byte[] decodedData = cipher.doFinal(encryptedData);
            return new String(decodedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String rasEncrypt(String inputStr) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(mAlias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
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
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(mAlias, null);
            RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
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

    private void logs() {
        try {
            Enumeration<String> aliases = mKeyStore.aliases();
            while (aliases.hasMoreElements()) {
                Log.i(TAG, "Key " + aliases.nextElement());
            }
        } catch (Exception ignored) {
        }
    }

}
