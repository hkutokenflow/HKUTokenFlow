package com.example.workshop1.Ethereum;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;
import java.io.InputStream;

public class KeystoreManager {
    private static final String TAG = "KeystoreManager";

    /**
     * Load credentials from Geth keystore file in assets folder
     * @param context Android context
     * @param keystoreFileName Name of keystore file in assets
     * @param password Keystore password
     * @return Credentials object or null if failed
     */
    public static Credentials loadCredentialsFromKeystore(Context context, String keystoreFileName, String password) {
        try {
            // Read keystore file from assets
            AssetManager assetManager = context.getAssets();
            InputStream keystoreStream = assetManager.open(keystoreFileName);

            // Convert InputStream to String
            byte[] keystoreBytes = new byte[keystoreStream.available()];
            keystoreStream.read(keystoreBytes);
            keystoreStream.close();

            String keystoreContent = new String(keystoreBytes);

            // Load credentials using Web3j
            Credentials credentials = WalletUtils.loadJsonCredentials(password, keystoreContent);

            Log.d(TAG, "Successfully loaded credentials from keystore: " + keystoreFileName);
            Log.d(TAG, "Loaded address: " + credentials.getAddress());

            return credentials;

        } catch (IOException e) {
            Log.e(TAG, "Error reading keystore file: " + keystoreFileName, e);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error loading credentials from keystore (wrong password?): " + keystoreFileName, e);
            return null;
        }
    }

    /**
     * Validate that loaded credentials match expected address
     * @param credentials Loaded credentials
     * @param expectedAddress Expected admin address
     * @return true if addresses match
     */
    public static boolean validateCredentials(Credentials credentials, String expectedAddress) {
        if (credentials == null) {
            Log.e(TAG, "Credentials are null");
            return false;
        }

        String actualAddress = credentials.getAddress();

        // Normalize addresses for comparison (remove 0x prefix, convert to lowercase)
        String normalizedActual = actualAddress.toLowerCase().replace("0x", "");
        String normalizedExpected = expectedAddress.toLowerCase().replace("0x", "");

        boolean isValid = normalizedActual.equals(normalizedExpected);

        if (!isValid) {
            Log.e(TAG, "Address mismatch!");
            Log.e(TAG, "Expected: " + expectedAddress);
            Log.e(TAG, "Got:      " + actualAddress);
        } else {
            Log.d(TAG, "Credentials validated successfully for address: " + expectedAddress);
        }

        return isValid;
    }

    /**
     * Load and validate admin credentials
     * @param context Android context
     * @param password Keystore password
     * @return Valid admin credentials or null
     */
    public static Credentials loadAdminCredentials(Context context, String password) {
        Credentials credentials = loadCredentialsFromKeystore(
                context,
                BlockchainConfig.ADMIN_KEYSTORE_FILE,
                password
        );

        if (credentials != null && validateCredentials(credentials, BlockchainConfig.ADMIN_ADDRESS)) {
            return credentials;
        }

        return null;
    }
}