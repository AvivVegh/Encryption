package com.avivvegh.encryption;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
class EncryptorAboveApi18 extends BaseEncryptor implements Encryptor {
    //region Const

    private static final String AES_KEY = "AXL";

    //endregion

    //region Private members

    private KeyStore keyStore;

    //endregion

    //region C'tor

    EncryptorAboveApi18(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);

        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_TYPE);
            keyStore.load(null);

            if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {

                // Generate the RSA key pairs
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, RSA_CALENDAR_AMOUNT);

                KeyPairGeneratorSpec keyPairGeneratorSpec =
                        new KeyPairGeneratorSpec.Builder(context)
                                .setAlias(KEYSTORE_ALIAS)
                                .setSubject(new X500Principal(X500_PRINCIPAL_NAME))
                                .setSerialNumber(BigInteger.TEN)
                                .setStartDate(start.getTime())
                                .setEndDate(end.getTime())
                                .build();

                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA,
                        ANDROID_KEY_STORE_TYPE);
                keyPairGenerator.initialize(keyPairGeneratorSpec);
                keyPairGenerator.generateKeyPair();

                // generate AES key, encrypt it with RSA public key, and save the encrypted AES key
                generateAndStoreAESKey();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Private methods

    private void generateAndStoreAESKey() {
        String encryptedKeyB64 = sharedPreferences.getString(AES_KEY,
                null);

        if (encryptedKeyB64 == null) {
            // Generate key
            byte[] key = new byte[KEY_LENGTH_IN_BYTES];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(key);
            // Encrypt the key with RSA
            byte[] encryptedKey = rsaEncrypt(key);
            encryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT);

            sharedPreferences.edit().putString(AES_KEY, encryptedKeyB64).commit();
        }
    }

    private byte[] rsaEncrypt(byte[] plainText) {
        // Encrypt plaintText using RSA public key.
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry =
                    (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYSTORE_ALIAS, null);
            Cipher cipher = Cipher.getInstance(RSA_MODE, RSA_CIPHER_PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream =
                    new CipherOutputStream(byteArrayOutputStream, cipher);
            cipherOutputStream.write(plainText);
            cipherOutputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    private byte[] rsaDecrypt(byte[] encryptedText) {
        // Decrypt plaintText using RSA private key.
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry =
                    (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYSTORE_ALIAS, null);
            Cipher cipher = Cipher.getInstance(RSA_MODE, RSA_CIPHER_PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
            CipherInputStream cipherInputStream =
                    new CipherInputStream(new ByteArrayInputStream(encryptedText), cipher);

            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];

            for (int index = 0; index < bytes.length; index++) {
                bytes[index] = values.get(index);
            }

            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    //endregion

    //region Factory methods

    @Override
    public Key getSecretKey() {
        String encryptedKeyB64 = sharedPreferences.getString(AES_KEY, null);

        if (encryptedKeyB64 == null) {
            generateAndStoreAESKey();
        }

        byte[] encryptedKey = Base64.decode(encryptedKeyB64, Base64.DEFAULT);
        byte[] key = rsaDecrypt(encryptedKey);

        return new SecretKeySpec(key, KEY_ALGORITHM_AES);
    }

    @Override
    public String decrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);

            // GCMParameterSpec available only from API 21
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cipher.init(Cipher.DECRYPT_MODE, getSecretKey(),
                        new GCMParameterSpec(GCM_TAG_LENGTH, getIV()));
            } else {
                cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), new IvParameterSpec(getIV()));
            }

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(),
                        new GCMParameterSpec(GCM_TAG_LENGTH, getIV()));
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), new IvParameterSpec(getIV()));
            }
            byte[] encodeBytes = cipher.doFinal(text.getBytes());

            return Base64.encodeToString(encodeBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    //endregion
}
