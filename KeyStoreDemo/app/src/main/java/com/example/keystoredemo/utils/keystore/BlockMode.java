package com.example.keystoredemo.utils.keystore;

public enum BlockMode {
    /**
     * Electronic Codebook (ECB) block mode.
     */
    ECB("ECB"),
    /**
     * Cipher Block Chaining (CBC) block mode.
     */
    CBC("CBC"),
    /**
     * Counter (CTR) block mode.
     */
    CTR("CTR"),
    /**
     * Galois/Counter Mode (GCM) block mode.
     */
    GCM("GCM");

    public String rawValue;

    BlockMode(String blockMode) {
        this.rawValue = blockMode;
    }
}
