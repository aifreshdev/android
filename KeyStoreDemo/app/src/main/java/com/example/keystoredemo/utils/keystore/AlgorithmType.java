package com.example.keystoredemo.utils.keystore;

import android.annotation.TargetApi;
import android.os.Build;

public enum AlgorithmType {

    /**
     * Default algorithms
     * more : https://developer.android.com/training/articles/keystore#SupportedCiphers
     */

    /**
     * AES is support only above API Lv23.
     */
    @TargetApi(Build.VERSION_CODES.M)
    AES("AES"),

    /**
     * RSA is support only above API Lv18.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    RSA("RSA");

    public String rawValue;

    AlgorithmType(String type) {
        this.rawValue = type;
    }
}
