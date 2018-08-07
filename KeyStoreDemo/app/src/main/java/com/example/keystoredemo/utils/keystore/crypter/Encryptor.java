package com.example.keystoredemo.utils.keystore.crypter;

import com.example.keystoredemo.utils.keystore.KeyStoreWrapper;

import javax.crypto.Cipher;

public class Encryptor {

//    private final String HASH_ALGORITHM = "SHA-256";
//    private KeyStoreWrapper mKsBuilder;
//    private Cipher mCipher;
//    private Builder mBuilder;
//    private byte[] mIvByte;

    private static Encryptor sInstance;

//    public static Encryptor getInstance(Context context, AlgorithmType type){
//        if(sInstance == null){
//            sInstance = new Encryptor(new KeyStoreWrapper.Builder(context)
//                    .setAlgorithmType(type)
//                    .setBlockMode(BlockMode.ECB)
//                    .setAlgorithmPadding(EncryptionPadding.NONE));
//        }
//
//        return sInstance;
//    }
//
//    private Encryptor(Builder builder){
////        mBuilder = builder;
////
////        if(!hasMarshmallow() &&
////                !"ECB".equalsIgnoreCase(mBuilder.getBlockMode())){
////            mBuilder.setBlockMode(BlockMode.ECB);
////        }
////
////        mKsBuilder = new KeyStoreWrapper(mBuilder);
////
////        try {
////            mCipher = Cipher.getInstance(mBuilder.getType() + "/"
////                    + mBuilder.getBlockMode() + "/"
////                    + mBuilder.getAlgorithmPadding());
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }
//
//    private SecretKeySpec getSecretKeySpec(String password) {
//        try {
//            final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
//            byte[] bytes = password.getBytes("UTF-8");
//            digest.update(bytes, 0, bytes.length);
//            byte[] key = digest.digest();
//            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
//            return secretKeySpec;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//
//    private byte[] getRandomIv() {
//        SecureRandom r = new SecureRandom();
//        byte[] ivBytes = new byte[16];
//        r.nextBytes(ivBytes);
//        return ivBytes;
//    }
//
//    private String bytesToHex(byte[] data) {
//        StringBuilder builder = new StringBuilder();
//        for (byte b : data) {
//            builder.append(String.format("%02x", b));
//        }
//        return builder.toString();
//    }
//
//    private byte[] hexToBytes(String data) {
//        if (data == null || data.length() < 2) {
//            return new byte[0];
//        }
//
//        byte[] byteResult = new byte[data.length() / 2];
//        for (int i = 0; i < byteResult.length - 1; i++) {
//            int index = i * 2;
//            String str = data.substring(index, index + 2); // e.g ab "cd" ef "gh"
//            byteResult[i] = (byte) Integer.parseInt(str, 16);
//        }
//        return byteResult;
//    }
//
//    private boolean hasJellyBean(){
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
//    }
//
//    private boolean hasMarshmallow(){
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
//    }

//    public static class Builder {
//
//        private Context mContext;
//
//        private AlgorithmType mAlgorithmType = AlgorithmType.RSA;
//        private EncryptionPadding mAlgorithmPadding = EncryptionPadding.NONE;
//        private BlockMode mBlockMode = BlockMode.CBC;
//
//        private String mType = "AES";
//        private String mAlias = "secret";
//
//        public Builder(Context context) {
//            mContext = context;
//        }
//
//        public Builder setAlias(String alias) {
//            this.mAlias = alias;
//            return this;
//        }
//
//        public Builder setType(String type) {
//            this.mType = type;
//            return this;
//        }
//
//        public Builder setAlgorithmType(AlgorithmType type) {
//            this.mAlgorithmType = type;
//            return this;
//        }
//
//        public Builder setBlockMode(BlockMode mode) {
//            this.mBlockMode = mode;
//            return this;
//        }
//
//        public Builder setAlgorithmPadding(EncryptionPadding padding) {
//            this.mAlgorithmPadding = padding;
//            return this;
//        }
//
//        public Context getContext() {
//            return mContext;
//        }
//
//        public String getAlias() {
//            return mAlias;
//        }
//
//        public String getType() {
//            return mType;
//        }
//
//        public String getBlockMode() {
//            return mBlockMode.blockMode;
//        }
//
//        public String getAlgorithmType() {
//            return mAlgorithmType.type;
//        }
//
//        public String getAlgorithmPadding() {
//            return mAlgorithmPadding.padding;
//        }
//
//        public Builder build() {
//            return this;
//        }
//    }
}
