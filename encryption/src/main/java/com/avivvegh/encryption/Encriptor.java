package com.avivvegh.encryption;

import java.security.Key;

interface Encriptor {
    Key getSecretKey();
    String decrypt(String text);
    String encrypt(String text);
}
