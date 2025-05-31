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
            // Load the smart contract using unlocked account from Geth
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

    // ------------------------ Transactions ------------------------

    public void mintTokens(String toAddress, BigInteger amount) {
        boolean manualMiningStart = false;
        try {                
            long startTime = System.currentTimeMillis();
            BigInteger wei = convertTokensToWei(amount);

            if (!isMiningActive()) {
                boolean startSuccess = GethMiningController.startMining(4).get();
                if (!startSuccess) {
                    Log.e("Ethereum Manager", "Failed to start mining");
                    return;
                }
                manualMiningStart = true;
                waitForMiningToStart();
            } else {
                Log.d("Ethereum Manager", "Mining already active, proceeding with transaction");
            }
            
            TransactionReceipt receipt = contract.mintTokens(toAddress, wei).send();
            long endTime = System.currentTimeMillis();
            Log.d("Ethereum Manager", "Tokens minted successfully: " + receipt.getTransactionHash());
            Log.d("Ethereum Manager", "Transaction mined in " + (endTime - startTime) + "ms");
            Log.d("Ethereum Manager", "Transaction hash: " + receipt.getTransactionHash());
            Log.d("Ethereum Manager", "Block number: " + receipt.getBlockNumber());
            Log.d("Ethereum Manager", "Gas used: " + receipt.getGasUsed());

        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error minting tokens: " + e.getMessage());
        } finally {
            if (manualMiningStart) {
                try {
                    GethMiningController.stopMining();
                    Log.d("Ethereum Manager", "Stopped mining (started by this transaction)");
                } catch (Exception e) {
                    Log.e("Ethereum Manager", "Error stopping manual mining: " + e.getMessage());
                }
            }
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

    // ------------------------ Manage roles ------------------------

    public void assignRole(String userAddress, String roleType) {
        if (!roleType.equals("STUDENT") && !roleType.equals("VENDOR")) {
            Log.e("Ethereum Manager", "Invalid role type: " + roleType);
            return;
        }
        
        new Thread(() -> {
            boolean manualMiningStart = false;
            try {
                if (!isMiningActive()) {
                    boolean startSuccess = GethMiningController.startMining(4).get();
                    if (!startSuccess) {
                        Log.e("Ethereum Manager", "Failed to start mining for role assignment");
                        return;
                    }
                    manualMiningStart = true;
                    waitForMiningToStart();
                }
    
                TransactionReceipt receipt = contract.assignRole(userAddress, roleType).send();
                Log.d("Ethereum Manager", roleType + " role assigned successfully");
                Log.d("Ethereum Manager", "Address: " + userAddress);
                Log.d("Ethereum Manager", "Transaction hash: " + receipt.getTransactionHash());

            } catch (Exception e) {
                Log.e("Ethereum Manager", "Error assigning " + roleType + " role: " + e.getMessage());
            } finally {
                if (manualMiningStart) {
                    try {
                        GethMiningController.stopMining();
                    } catch (Exception e) {
                        Log.e("Ethereum Manager", "Error stopping mining: " + e.getMessage());
                    }
                }
            }
        }).start();
    }
    

    // ------------------------ Mining ------------------------

    private boolean isMiningActive() {
        try {
            boolean isMining = web3j.ethMining().send().isMining();
            Log.d("Ethereum Manager", "Direct mining check: " + isMining);
            return isMining;
        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error checking mining status: " + e.getMessage());
            return false; 
        }
    }

    private void waitForMiningToStart() throws InterruptedException {
        int maxWaitTime = 3000; // Max 3 seconds
        int checkInterval = 200; // Check every 200ms
        int totalWaited = 0;
        
        while (totalWaited < maxWaitTime) {
            if (isMiningActive()) {
                Log.d("Ethereum Manager", "Mining confirmed active after " + totalWaited + "ms");
                return;
            }
            Thread.sleep(checkInterval);
            totalWaited += checkInterval;
        }
        Log.d("Ethereum Manager", "Mining status unclear after " + maxWaitTime + "ms, proceeding anyway");
    }

    // ------------------------ Utils ------------------------

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

    // Get current network chain ID (for debugging)
    public void logChainId() {
        try {
            BigInteger chainId = web3j.ethChainId().send().getChainId();
            Log.d("Ethereum Manager", "Network Chain ID: " + chainId);
        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error getting chain ID: " + e.getMessage());
        }
    }

}


/* test blockchain connection

    new Thread(() -> {
        Web3j web3 = Web3j.build(new HttpService(BLOCKCHAIN_URL));
        try {
            ethereumManager = new EthereumManager(this);
            ethereumManager.checkTokenDecimals();

            BigInteger blockNumber = web3.ethBlockNumber().send().getBlockNumber();
            Log.d("Blockchain", "Latest Ethereum block number: " + blockNumber);

            // get account ETH balance
            EthGetBalance ethGetBalance = web3.ethGetBalance("0xa3b630aa86b171da5c767fcbd16e76f1082ed9f4",
                    DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger wei = ethGetBalance.getBalance();
            java.math.BigDecimal eth = Convert.fromWei(String.valueOf(wei), Convert.Unit.ETHER);
            Log.d("Blockchain", "ETH balance: " + eth);

            // test smart contract, get token balance, mining
            BigInteger balance = ethereumManager.getBalance("0xa3b630aa86b171da5c767fcbd16e76f1082ed9f4");
            Log.d("Ethereum Manager", "Original token balance: " + balance);
            BigInteger mint = BigInteger.valueOf(1);
            Log.d("Ethereum Manager", "To mint: " + mint);

            Log.d("Ethereum Manager", "Starting mining for transaction");
            ethereumManager.mintTokens("0xa3b630aa86b171da5c767fcbd16e76f1082ed9f4", mint);
            BigInteger newBalance = ethereumManager.getBalance("0xa3b630aa86b171da5c767fcbd16e76f1082ed9f4");
            Log.d("Ethereum Manager", "New token balance: " + newBalance);

        } catch (IOException e) {
            Log.e("Blockchain Error", "Failed to connect", e);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }).start();

 */
