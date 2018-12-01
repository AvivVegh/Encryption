package com.avivvegh.encryption;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.security.SecureRandom;

abstract class BaseEncriptor {

    // region Const

    static final String ANDROID_KEY_STORE_TYPE = "AndroidKeyStore";
    static final String AES_MODE = "AES/GCM/NoPadding";
    static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
    static final String KEYSTORE_ALIAS = "EN_KEYSTORE";
    static final String X500_PRINCIPAL_NAME = "CN=" + KEYSTORE_ALIAS;
    static final String RSA_CIPHER_PROVIDER = "AndroidOpenSSL";
    static final int RSA_CALENDAR_AMOUNT = 30;
    static final int KEY_LENGTH_IN_BYTES = 16;
    private static final int KEY_IV_LENGTH_IN_BYTES = 12;
    static final int GCM_TAG_LENGTH = 128;
    static final String KEY_ALGORITHM_AES = "AES";
    static final String KEY_ALGORITHM_RSA = "RSA";
    private static final String IV_X = "QWEX";
    
    //endregion

    SharedPreferences sharedPreferences;
    Context applicationContext;

    BaseEncriptor(Context applicationContext, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.applicationContext = applicationContext;
        generateAndSaveIV();
    }

    void generateAndSaveIV() {
        if (sharedPreferences.getString(IV_X, "").equals("")) {
            SecureRandom secureRandom = new SecureRandom();
            byte[] ivBytes = new byte[KEY_IV_LENGTH_IN_BYTES];
            secureRandom.nextBytes(ivBytes);
            String iv = Base64.encodeToString(ivBytes, Base64.DEFAULT);
            sharedPreferences.edit().putString(IV_X, iv).commit();
        }
    }

    byte[] getIV() {
        String prefIV = sharedPreferences.getString(IV_X, "");
        return Base64.decode(prefIV, Base64.DEFAULT);
    }
}
