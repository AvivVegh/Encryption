package com.avivvegh.encryption;

import java.security.Key;

interface Encryptor {
    Key getSecretKey();
    String decrypt(String text);
    String encrypt(String text);
}
