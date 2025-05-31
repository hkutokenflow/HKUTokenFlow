package com.example.workshop1.Ethereum;

import android.content.Context;
import android.util.Log;

import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.contracts.Sc_test;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.core.methods.response.BooleanResponse;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.math.BigDecimal;

public class EthereumManager {
    private static final String BLOCKCHAIN_URL = BlockchainConfig.BLOCKCHAIN_URL;
    private static final String PRIVATE_KEY = BlockchainConfig.ADMIN_PRIVATE_KEY;
    private static final String CONTRACT_ADDRESS = BlockchainConfig.TOKEN_CONTRACT_ADDRESS;
    private static final Credentials credentials = Credentials.create(PRIVATE_KEY);
    private static final long CHAIN_ID = 1234;

    private final Web3j web3j;
    private final Admin admin;
    private final ContractGasProvider gasProvider;
    private final Mysqliteopenhelper mysqliteopenhelper;
    private final Context context;
    private Sc_test contract;
    private final TransactionManager transactionManager;

    private BigInteger tokenDecimals = null;

    public EthereumManager(Context context) {
        this.context = context;
        this.mysqliteopenhelper = new Mysqliteopenhelper(context);

        HttpService httpService = new HttpService(BLOCKCHAIN_URL);
        web3j = Web3j.build(httpService);
        admin = Admin.build(httpService); 

        gasProvider = new DefaultGasProvider();

        transactionManager = new RawTransactionManager(web3j, credentials, CHAIN_ID);

        try {
            // Load the smart contract using the unlocked account from Geth
            contract = Sc_test.load(
                    CONTRACT_ADDRESS,
                    web3j,
                    transactionManager,
                    gasProvider
            );
            Log.d("Ethereum Manager", "Smart contract loaded successfully");
        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error loading smart contract: " + e.getMessage());
        }

        loadTokenDecimals();
    }

    // Get and cache token decimals (call this once during initialization)
    private void loadTokenDecimals() {
        try {
            tokenDecimals = contract.decimals().send();
            Log.d("Ethereum Manager", "Token decimals loaded: " + tokenDecimals);
        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error loading token decimals: " + e.getMessage());
            tokenDecimals = BigInteger.valueOf(18); // Default to 18 decimals
        }
    }

    // Convert token amount to wei
    public BigInteger convertTokensToWei(BigInteger tokenAmount) {
        if (tokenDecimals == null) {
            loadTokenDecimals();
        }
        return tokenAmount.multiply(BigInteger.TEN.pow(tokenDecimals.intValue()));
    }

    // Convert wei to token amount
    public BigInteger convertWeiToTokens(BigInteger wei) {
        if (tokenDecimals == null) {
            loadTokenDecimals();
        }
        return wei.divide(BigInteger.TEN.pow(tokenDecimals.intValue()));
    }

    public void mintTokens(String toAddress, BigInteger amount) {
        try {                
            BigInteger wei = convertTokensToWei(amount);
            
            long startTime = System.currentTimeMillis();
            boolean startSuccess = GethMiningController.startMining(1).get();
            if (!startSuccess) {
                Log.e("Ethereum Manager", "Failed to start mining");
                return;
            }
            Thread.sleep(2000);

            TransactionReceipt receipt = contract.mintTokens(toAddress, wei).send();
            long endTime = System.currentTimeMillis();
            Log.d("Ethereum Manager", "Tokens minted successfully: " + receipt.getTransactionHash());
            Log.d("Ethereum Manager", "Transaction mined in " + (endTime - startTime) + "ms");
            Log.d("Ethereum Manager", "Transaction hash: " + receipt.getTransactionHash());
            Log.d("Ethereum Manager", "Block number: " + receipt.getBlockNumber());
            Log.d("Ethereum Manager", "Gas used: " + receipt.getGasUsed());

            GethMiningController.stopMining();

        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error minting tokens: " + e.getMessage());
        }
    }

    public BigInteger getBalance(String address) {
        try {
            BigInteger tokenUnits = contract.balanceOf(address).send();
            BigInteger tokenBalance = convertWeiToTokens(tokenUnits);
            return tokenBalance;
            
        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error getting balance: " + e.getMessage());
            return BigInteger.ZERO;
        }
    }

    // Get current network chain ID (for debugging)
    public void logChainId() {
        try {
            BigInteger chainId = web3j.ethChainId().send().getChainId();
            Log.d("Ethereum Manager", "Network Chain ID: " + chainId);
        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error getting chain ID: " + e.getMessage());
        }
    }

    public void checkTokenDecimals() {
        new Thread(() -> {
            try {
                BigInteger decimals = contract.decimals().send();
                Log.d("Token Info", "Token decimals: " + decimals);
                
                // Calculate the conversion factor
                BigInteger conversionFactor = BigInteger.TEN.pow(decimals.intValue());
                Log.d("Token Info", "1 token = " + conversionFactor + " smallest units");
                Log.d("Token Info", "Conversion factor: 10^" + decimals);
                
            } catch (Exception e) {
                Log.e("Token Info", "Error checking decimals: " + e.getMessage());
            }
        }).start();
    }

}
