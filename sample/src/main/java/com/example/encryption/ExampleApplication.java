package com.example.encryption;

import android.app.Application;
import com.avivvegh.encryption.EncryptionManager;

public class ExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize
        EncryptionManager.initialize(this);
    }
}
