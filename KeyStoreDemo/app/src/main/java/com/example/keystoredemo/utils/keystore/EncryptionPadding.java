package com.example.keystoredemo.utils.keystore;

public enum EncryptionPadding {

    /*
     * More : https://developer.android.com/training/articles/keystore#SupportedCiphers
     */

    /**
     * AES/CBC/PKCS7Padding
     * RSA/ECB/PKCS1Padding
     * support only above API 18+
     */
    PKCS1("PKCS1Padding"),

    /**
     * AES/CBC/PKCS7Padding
     * AES/ECB/PKCS7Padding
     * support only above API 23+
     */
    PKCS7("PKCS7Padding"),
    /**
     * RSA/ECB/NoPadding
     * support only above API 18+
     */
    NONE("NoPadding");

    public String rawValue;

    EncryptionPadding(String padding){
        this.rawValue = padding;
    }
}
