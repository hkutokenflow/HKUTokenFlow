package com.example.workshop1.Ethereum;

import android.util.Log;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.List;

public class AccountManager {
    private static final String TAG = "AccountManager";
    private static final String BLOCKCHAIN_URL = BlockchainConfig.BLOCKCHAIN_URL;
    
    private final Admin admin;
    private final Web3j web3j;
    
    public AccountManager() {
        HttpService httpService = new HttpService(BLOCKCHAIN_URL);
        this.admin = Admin.build(httpService);
        this.web3j = Web3j.build(httpService);
    }
    
    /**
     * Creates a new account in geth's keystore
     * @param password Password to encrypt the account
     * @return The new account address or null if failed
     */
    public String createGethAccount(String password) {
        try {
            NewAccountIdentifier newAccount = admin.personalNewAccount(password).send();
            String address = newAccount.getAccountId();
            
            Log.d(TAG, "Created new account in geth keystore: " + address);
            return address;
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating account in geth: " + e.getClass().getSimpleName());
            Log.e(TAG, "Error message: " + e.getMessage());
            Log.e(TAG, "Error cause: " + (e.getCause() != null ? e.getCause().getMessage() : "No cause"));
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get all accounts from geth's keystore
     * @return List of account addresses
     */
    public List<String> getGethAccounts() {
        try {
            EthAccounts ethAccounts = web3j.ethAccounts().send();
            List<String> accounts = ethAccounts.getAccounts();
            
            Log.d(TAG, "Retrieved " + accounts.size() + " accounts from geth");
            return accounts;
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting geth accounts: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Unlock an account in geth
     * @param address Account address
     * @param password Account password
     * @param duration Duration to keep unlocked (seconds)
     * @return true if successful
     */
    public boolean unlockAccount(String address, String password, int duration) {
        try {
            boolean result = admin.personalUnlockAccount(address, password, BigInteger.valueOf(duration)).send().accountUnlocked();
            
            if (result) {
                Log.d(TAG, "Successfully unlocked account: " + address);
            } else {
                Log.w(TAG, "Failed to unlock account: " + address);
            }
            
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, "Error unlocking account: " + e.getMessage());
            return false;
        }
    }
} 