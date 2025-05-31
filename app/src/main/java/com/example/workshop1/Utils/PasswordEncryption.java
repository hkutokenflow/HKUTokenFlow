package com.example.workshop1.Utils;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;

public class PasswordEncryption {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // GCM认证标签长度
    private static final int IV_LENGTH_BYTE = 12;  // GCM推荐的IV长度
    private static final String KEY = "HKUTokenFlow2024"; // 32字节的密钥
    private static final SecretKey secretKey;

    static {
        // 初始化固定的密钥
        byte[] keyBytes = new byte[32]; // AES-256需要32字节密钥
        byte[] keyStringBytes = KEY.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(keyStringBytes, 0, keyBytes, 0, Math.min(keyStringBytes.length, 32));
        secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    // 加密密码
    public static String encrypt(String password) {
        try {
            // 生成随机IV
            byte[] iv = new byte[IV_LENGTH_BYTE];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            // 初始化GCM模式
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            // 加密数据
            byte[] cipherText = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

            // 组合IV和密文
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return Base64.encodeToString(combined, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 解密密码
    public static String decrypt(String encryptedPassword) {
        try {
            // 解码Base64数据
            byte[] combined = Base64.decode(encryptedPassword, Base64.NO_WRAP);

            // 提取IV
            byte[] iv = new byte[IV_LENGTH_BYTE];
            System.arraycopy(combined, 0, iv, 0, iv.length);

            // 提取密文
            byte[] cipherText = new byte[combined.length - IV_LENGTH_BYTE];
            System.arraycopy(combined, IV_LENGTH_BYTE, cipherText, 0, cipherText.length);

            // 初始化GCM模式
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            // 解密数据
            byte[] decryptedBytes = cipher.doFinal(cipherText);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 验证密码
    public static boolean verifyPassword(String inputPassword, String storedEncryptedPassword) {
        try {
            String decryptedPassword = decrypt(storedEncryptedPassword);
            return inputPassword.equals(decryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}