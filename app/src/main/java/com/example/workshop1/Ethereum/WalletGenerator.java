package com.example.workshop1.Ethereum;

import android.util.Log;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.ECKeyPair;

public class WalletGenerator {
    private static final String TAG = "WalletGenerator";

    /**
     * Generates a new Ethereum wallet address using Web3j's secure method
     * @return Ethereum wallet address or null if failed
     */
    public static String generateWalletAddress() {
        try {
            // Use Web3j's secure key generation
            ECKeyPair keyPair = Keys.createEcKeyPair();
            Credentials credentials = Credentials.create(keyPair);
            String address = credentials.getAddress();
            
            Log.d(TAG, "Generated secure wallet address: " + address);
            
            // Private key is automatically discarded when method ends
            return address;
            
        } catch (Exception e) {
            Log.e(TAG, "Error generating wallet address: " + e.getMessage());
            return null;
        }
    }
} 