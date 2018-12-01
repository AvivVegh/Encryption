package com.avivvegh.encryption;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

public class EncryptionManager {
    private static final String SHARED_PREFS_KEY =
            "com.avivvegh.encryption.SHARED_PREFS_KEY";

    private static EncryptionManager instance;

    private Encriptor encriptor;
    private static Context applicationContext;
    private SharedPreferences sharedPreferences;

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
            encriptor = new EncriptAboveApi23(applicationContext, sharedPreferences);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            encriptor = new EncriptAboveApi18(applicationContext, sharedPreferences);
        } else {
            encriptor = new EncriptBelowApi18(applicationContext, sharedPreferences);
        }
    }

    public String encrypt(String text) {
        return !TextUtils.isEmpty(text) ? encriptor.encrypt(text) : null;
    }

    public String decrypt(String text) {
        return !TextUtils.isEmpty(text) ? encriptor.decrypt(text) : null;
    }
}
