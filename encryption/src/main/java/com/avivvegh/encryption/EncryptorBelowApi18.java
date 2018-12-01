package com.avivvegh.encryption;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

class EncryptorBelowApi18 extends BaseEncryptor implements Encryptor {

    //region C'tor

    EncryptorBelowApi18(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    //endregion

    //region Factory methods

    @Override
    public Key getSecretKey() {
        try {
            String key = Long.toString(applicationContext.getPackageManager()
                    .getPackageInfo(applicationContext.getPackageName(),
                            0)
                    .firstInstallTime);

            byte[] fixesSizeBytesArray = new byte[KEY_LENGTH_IN_BYTES];

            for (int index = 0; index < Math.min(key.length(), fixesSizeBytesArray.length);
                 index++) {
                fixesSizeBytesArray[index] = key.getBytes()[index];
            }

            return new SecretKeySpec(fixesSizeBytesArray, KEY_ALGORITHM_AES);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String decrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), new IvParameterSpec(getIV()));

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
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), new IvParameterSpec(getIV()));
            byte[] encodeBytes = cipher.doFinal(text.getBytes());

            return Base64.encodeToString(encodeBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    //endregion
}
