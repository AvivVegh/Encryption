package com.avivvegh.encryption;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

public class EncryptionManager {

    //region Const

    private static final String SHARED_PREFS_KEY = "com.avivvegh.encryption.SHARED_PREFS_KEY";

    //endregion

    //region Private members

    private static EncryptionManager instance;
    private static Context applicationContext;

    private Encryptor encryptor;
    private SharedPreferences sharedPreferences;

    //endregion

    //region LifeCycle

    public static EncryptionManager getInstance() {
        if (instance == null) {
            instance = new EncryptionManager();
        }

        return instance;
    }

    public static void initalize(Application application) {
        if (application == null) {
            throw new RuntimeException("Application is null");
        }

        applicationContext = application.getApplicationContext();
    }

    private EncryptionManager() {
        if (applicationContext == null) {
            throw new RuntimeException("EncryptionManager is not initialize please call EncryptionManager.initalize");
        }

        sharedPreferences = applicationContext.
                getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            encryptor = new EncryptorAboveApi23(applicationContext, sharedPreferences);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            encryptor = new EncryptorAboveApi18(applicationContext, sharedPreferences);
        } else {
            encryptor = new EncryptorBelowApi18(applicationContext, sharedPreferences);
        }
    }

    //endregion

    //region Public methods

    public String encrypt(String text) {
        return !TextUtils.isEmpty(text) ? encryptor.encrypt(text) : null;
    }

    public String decrypt(String text) {
        return !TextUtils.isEmpty(text) ? encryptor.decrypt(text) : null;
    }

    //endregion
}
