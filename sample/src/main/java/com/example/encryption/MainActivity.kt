package com.example.encryption

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.avivvegh.encryption.EncryptionManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val encryptedString1 = EncryptionManager.getInstance().encrypt("123")
        val encryptedString2 = EncryptionManager.getInstance().encrypt("1234")
        Log.d("test", "encypted 1= " + encryptedString1)
        Log.d("test", "encypted 2= " + encryptedString2)

        val decryptedString1 = EncryptionManager.getInstance().decrypt(encryptedString1)
        val decryptedString2 = EncryptionManager.getInstance().decrypt(encryptedString2)

        Log.d("test", "decryped 1= " + decryptedString1)
        Log.d("test", "decryped 2= " + decryptedString2)
    }
}
