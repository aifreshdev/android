package com.example.keystoredemo.utils.keystore.listener;

import android.content.Context;

public interface CipherInterface {
    public String encrypt(String inputStr);
    public String decrypt(String inputStr);
}
