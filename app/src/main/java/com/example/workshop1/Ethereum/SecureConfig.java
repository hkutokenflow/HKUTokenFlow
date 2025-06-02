package com.example.workshop1.Ethereum;

import android.content.Context;
import android.util.Log;
import java.io.InputStream;
import java.util.Properties;

public class SecureConfig {
    private static final String TAG = "SecureConfig";
    private static final String CONFIG_FILE = "secure_config.properties";

    /**
     * Load admin private key from secure config file
     * File should be in assets/ but NOT checked into version control
     */
    public static String getAdminPrivateKey(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(CONFIG_FILE);
            Properties properties = new Properties();
            properties.load(inputStream);
            
            String privateKey = properties.getProperty("admin.private.key");
            inputStream.close();
            
            if (privateKey == null || privateKey.isEmpty()) {
                Log.e(TAG, "Admin private key not found in config file");
                return null;
            }
            
            Log.d(TAG, "Admin private key loaded from secure config");
            return privateKey;
            
        } catch (Exception e) {
            Log.e(TAG, "Error reading secure config: " + e.getMessage());
            return null;
        }
    }
}