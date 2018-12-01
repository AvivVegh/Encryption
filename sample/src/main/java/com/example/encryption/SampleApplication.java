package com.example.encryption;

import android.app.Application;
import com.avivvegh.encryption.EncryptionManager;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        EncryptionManager.initalize(this);
    }
}
