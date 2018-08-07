package com.example.keystoredemo.utils.keystore.crypter;

import android.content.Context;
import android.util.Base64;

import com.example.keystoredemo.utils.keystore.AlgorithmType;
import com.example.keystoredemo.utils.keystore.BlockMode;
import com.example.keystoredemo.utils.keystore.EncryptionPadding;
import com.example.keystoredemo.utils.keystore.KeyStoreWrapper;

import java.security.PrivateKey;

import javax.crypto.Cipher;

public class RSACipher extends KeyStoreWrapper {

    private String mAlias = "secret";
    private AlgorithmType mAlgorithmType = AlgorithmType.RSA;
    private BlockMode mBlockMode = BlockMode.ECB;
    private EncryptionPadding mEncryptionPadding = EncryptionPadding.NONE;

    public RSACipher(Context context) {
        createAsymmetricKey(context);
    }

    /**
     * @param inputStr
     * API 19 : cipher.provider.name = AndroidOpenSSL
     * API 23, 24 : cipher.provider.name = AndroidKeyStoreBCWorkaround
     * AndroidKeyStore does not provide RSA/ECB/PKCS1Padding
     * RSA keys sizes are: 512, 768, 1024, 2048, 3072, 4096
     * Ref : https://developer.android.com/training/articles/keystore#SupportedKeyPairGenerators
     * @return
     */

    public String encrypt(String inputStr) {
        try {
            Cipher cipher = Cipher.getInstance(getAlgorithmType() + "/"
                    + getBlockMode() + "/"
                    + getEncryptionPadding());
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
            byte[] bytes = cipher.doFinal(inputStr.getBytes("UTF-8"));
            return Base64.encodeToString(bytes, Base64.NO_WRAP | Base64.NO_PADDING);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String decrypt(String inputStr) {
        try {
            PrivateKey privateKey = getPrivateKey();
            Cipher cipher = Cipher.getInstance(getAlgorithmType() + "/"
                    + getBlockMode() + "/"
                    + getEncryptionPadding());

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encryptedData = Base64.decode(inputStr, Base64.NO_WRAP | Base64.NO_PADDING);
            byte[] decodedData = cipher.doFinal(encryptedData);
            return new String(decodedData, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getAlias() {
        return mAlias;
    }

    @Override
    public String getAlgorithmType() {
        return mAlgorithmType.rawValue;
    }

    @Override
    public String getBlockMode() {
        return mBlockMode.rawValue;
    }

    @Override
    public String getEncryptionPadding() {
        return mEncryptionPadding.rawValue;
    }
}
