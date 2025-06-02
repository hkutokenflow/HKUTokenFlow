package com.example.workshop1.Ethereum;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import org.web3j.crypto.Credentials;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.KeyStore;

public class SecurePrivateKeyManager {
    private static final String TAG = "SecurePrivateKeyManager";
    private static final String KEYSTORE_ALIAS = "AdminPrivateKeyAlias";
    private static final String PREFS_NAME = "secure_ethereum_prefs";
    private static final String ENCRYPTED_KEY_PREF = "encrypted_private_key";
    private static final String IV_PREF = "private_key_iv";

    /**
     * Store private key securely using Android Keystore (one-time setup)
     */
    public static boolean storePrivateKey(Context context, String privateKey) {
        try {
            // Generate or get existing Android Keystore key
            SecretKey secretKey = getOrCreateSecretKey();
            
            // Encrypt the private key
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] encryptedKey = cipher.doFinal(privateKey.getBytes());
            
            // Store encrypted key and IV in SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit()
                .putString(ENCRYPTED_KEY_PREF, Base64.encodeToString(encryptedKey, Base64.DEFAULT))
                .putString(IV_PREF, Base64.encodeToString(iv, Base64.DEFAULT))
                .apply();
                
            Log.d(TAG, "Private key stored securely");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error storing private key: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load credentials securely from Android Keystore
     */
    public static Credentials loadSecureCredentials(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String encryptedKey = prefs.getString(ENCRYPTED_KEY_PREF, null);
            String ivString = prefs.getString(IV_PREF, null);
            
            if (encryptedKey == null || ivString == null) {
                Log.w(TAG, "No stored private key found");
                return null;
            }
            
            // Get Android Keystore key and decrypt
            SecretKey secretKey = getOrCreateSecretKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec iv = new IvParameterSpec(Base64.decode(ivString, Base64.DEFAULT));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            
            byte[] decryptedKey = cipher.doFinal(Base64.decode(encryptedKey, Base64.DEFAULT));
            String privateKey = new String(decryptedKey);
            
            Log.d(TAG, "Private key loaded securely");
            return Credentials.create(privateKey);
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading private key: " + e.getMessage());
            return null;
        }
    }

    private static SecretKey getOrCreateSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        
        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            // Generate new key with hardware-backed security
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(false) // Set to true for biometric auth
                .build();
            keyGenerator.init(keyGenParameterSpec);
            keyGenerator.generateKey();
            Log.d(TAG, "New Android Keystore key generated");
        }
        
        return (SecretKey) keyStore.getKey(KEYSTORE_ALIAS, null);
    }

    /**
     * Check if private key is already stored
     */
    public static boolean isPrivateKeyStored(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.contains(ENCRYPTED_KEY_PREF) && prefs.contains(IV_PREF);
    }
}