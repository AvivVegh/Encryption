package com.avivvegh.encryption;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import java.security.Key;
import java.security.KeyStore;

@RequiresApi(api = Build.VERSION_CODES.M)
class EncryptorAboveApi23 extends BaseEncryptor implements Encryptor {

    //region Private members

    private KeyStore keyStore;

    //endregion

    //region C'tor

    EncryptorAboveApi23(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);

        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_TYPE);
            keyStore.load(null);

            if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {

                // KeyGenerator class provides the functionality of a secret symmetric key generator
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES
                        , ANDROID_KEY_STORE_TYPE);
                keyGenerator.init(new KeyGenParameterSpec.Builder(KEYSTORE_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(false)
                        .build());

                keyGenerator.generateKey();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Factory methods

    @Override
    public Key getSecretKey() {
        try {
            return keyStore.getKey(KEYSTORE_ALIAS, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String decrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(),
                    new GCMParameterSpec(GCM_TAG_LENGTH, getIV()));

            return new String(cipher.doFinal(Base64.decode(text, Base64.DEFAULT)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public String encrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);
            // GCMParameterSpec available only from API 21
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(),
                    new GCMParameterSpec(GCM_TAG_LENGTH, getIV()));
            byte[] encodeBytes = cipher.doFinal(text.getBytes());

            return Base64.encodeToString(encodeBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    //endregion
}
