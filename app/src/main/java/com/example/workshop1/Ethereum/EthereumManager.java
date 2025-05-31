package com.example.workshop1.Ethereum;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.contracts.Sc_test;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.BooleanResponse;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import java.util.*;
import java.text.SimpleDateFormat;

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

    // ------------------------ Wallet interactions ------------------------

    // Mint tokens to wallet address
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

    // Get wallet balance
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

    // Get user wallet transaction records
    public List<BlockchainTransaction> getWalletTransactionHistory(String walletAddress) {
        List<BlockchainTransaction> transactions = new ArrayList<>();

        Log.d("EthereumManager", "=== Starting getWalletTransactionHistory ===");
        Log.d("EthereumManager", "Wallet address: " + walletAddress);
        
        try {           
            // Get all transfer events to this address (receiver = address, includes minting)
            Log.d("EthereumManager", "--- Searching for Transfer events TO address ---");
            List<Sc_test.TransferEventResponse> transferToEvents = getTransferEventsTo(walletAddress);
            Log.d("EthereumManager", "Found " + transferToEvents.size() + " Transfer TO events");

            for (Sc_test.TransferEventResponse event : transferToEvents) {
                Log.d("EthereumManager", "Processing Transfer TO event:");
                Log.d("EthereumManager", "  - Block: " + event.log.getBlockNumber());
                Log.d("EthereumManager", "  - From: " + event.from);
                Log.d("EthereumManager", "  - To: " + event.to);
                Log.d("EthereumManager", "  - Value (wei): " + event.value);

                String timestamp = getBlockTimestamp(event.log.getBlockNumber());

                // minting (from zero address)
                if (event.from.equals("0x0000000000000000000000000000000000000000")) {
                    // Get event name from SQLite database
                    String eventName = getEventNameFromBlockchainData(walletAddress, event.value, timestamp);
                    String description = eventName != null ? 
                        "Event Check-in: " + eventName : 
                        "Event Check-in";
                    
                    BlockchainTransaction tx = new BlockchainTransaction(
                        event.log.getBlockNumber(),
                        timestamp,
                        description,
                        "+" + convertWeiToTokens(event.value).toString(),
                        "MINT"
                    );
                    transactions.add(tx);
                    Log.d("EthereumManager", "  - Added MINTING transaction: " + tx.description + " " + tx.amount);
                } else {
                    // Regular transfers to address
                    BlockchainTransaction tx = new BlockchainTransaction(
                        event.log.getBlockNumber(),
                        timestamp,
                        "Transfer Received",
                        "+" + convertWeiToTokens(event.value).toString(),
                        "TRANSFER_IN"
                    );
                    transactions.add(tx);
                    Log.d("EthereumManager", "  - Added TRANSFER transaction: " + tx.description + " " + tx.amount);
                }
            }

            // Get all transfer events from this address (sender = address)
            Log.d("EthereumManager", "--- Searching for Transfer events FROM address ---");
            List<Sc_test.TransferEventResponse> transferFromEvents = getTransferEventsFrom(walletAddress);
            Log.d("EthereumManager", "Found " + transferFromEvents.size() + " Transfer FROM events");
            
            for (Sc_test.TransferEventResponse event : transferFromEvents) {
                Log.d("EthereumManager", "Processing Transfer FROM event:");
                Log.d("EthereumManager", "  - Block: " + event.log.getBlockNumber());
                Log.d("EthereumManager", "  - From: " + event.from);
                Log.d("EthereumManager", "  - To: " + event.to);
                Log.d("EthereumManager", "  - Value (wei): " + event.value);

                String timestamp = getBlockTimestamp(event.log.getBlockNumber());
                BlockchainTransaction tx = new BlockchainTransaction(
                    event.log.getBlockNumber(),
                    timestamp,
                    "Transfer Sent",
                    "-" + convertWeiToTokens(event.value).toString(),
                    "TRANSFER_OUT"
                );
                transactions.add(tx);
                Log.d("EthereumManager", "  - Added transfer: " + tx.description + " " + tx.amount);
            }
            
            // Sort by block number (newest first)
            Log.d("EthereumManager", "--- Sorting " + transactions.size() + " total transactions ---");
            transactions.sort((a, b) -> b.blockNumber.compareTo(a.blockNumber));

            Log.d("EthereumManager", "=== Final transaction list ===");
            for (int i = 0; i < transactions.size(); i++) {
                BlockchainTransaction tx = transactions.get(i);
                Log.d("EthereumManager", "Transaction " + (i+1) + ": " + tx.description + " | " + tx.amount + " | " + tx.timestamp + " | Block: " + tx.blockNumber);
            }
            
        } catch (Exception e) {
            Log.e("EthereumManager", "Error getting transaction history: " + e.getMessage());
            Log.e("EthereumManager", "Exception details: ", e);
        }
    
        return transactions;
    }

    // Get TokensMinted events for a specific address
    private List<Sc_test.TokensMintedEventResponse> getMintedEvents(String address) throws Exception {
        EthFilter filter = new EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            CONTRACT_ADDRESS
        );
        
        filter.addSingleTopic(org.web3j.abi.EventEncoder.encode(Sc_test.TOKENSMINTED_EVENT));
        filter.addSingleTopic(null); // Second topic is the 'to' address (indexed)
        filter.addOptionalTopics("0x000000000000000000000000" + address.substring(2)); // Format address as topic
        
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<Sc_test.TokensMintedEventResponse> events = new ArrayList<>();
        
        for (EthLog.LogResult<?> logResult : ethLog.getLogs()) {
            org.web3j.protocol.core.methods.response.Log log = (org.web3j.protocol.core.methods.response.Log) logResult.get();
            events.add(Sc_test.getTokensMintedEventFromLog(log));
        }
        
        return events;
    }

    // Get Transfer events where receiver = address
    private List<Sc_test.TransferEventResponse> getTransferEventsTo(String address) throws Exception {
        EthFilter filter = new EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            CONTRACT_ADDRESS
        );
        
        filter.addSingleTopic(org.web3j.abi.EventEncoder.encode(Sc_test.TRANSFER_EVENT));
        filter.addSingleTopic(null); // 'from' address (skip)
        filter.addOptionalTopics("0x000000000000000000000000" + address.substring(2)); // 'to' address
        
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<Sc_test.TransferEventResponse> events = new ArrayList<>();
        
        for (EthLog.LogResult<?> logResult : ethLog.getLogs()) {
            org.web3j.protocol.core.methods.response.Log log = (org.web3j.protocol.core.methods.response.Log) logResult.get();
            events.add(Sc_test.getTransferEventFromLog(log));
        }
        
        return events;
    }

    // Get Transfer events where sender = address
    private List<Sc_test.TransferEventResponse> getTransferEventsFrom(String address) throws Exception {
        EthFilter filter = new EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            CONTRACT_ADDRESS
        );
        
        filter.addSingleTopic(org.web3j.abi.EventEncoder.encode(Sc_test.TRANSFER_EVENT));
        filter.addOptionalTopics("0x000000000000000000000000" + address.substring(2)); // 'from' address
        
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<Sc_test.TransferEventResponse> events = new ArrayList<>();
        
        for (EthLog.LogResult<?> logResult : ethLog.getLogs()) {
            org.web3j.protocol.core.methods.response.Log log = (org.web3j.protocol.core.methods.response.Log) logResult.get();
            events.add(Sc_test.getTransferEventFromLog(log));
        }
        
        return events;
    }

    // Get block timestamp
    private String getBlockTimestamp(BigInteger blockNumber) {
        try {
            BigInteger timestamp = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), false).send().getBlock().getTimestamp();
            
            Date date = new Date(timestamp.longValue() * 1000); // Convert to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            return sdf.format(date);
        } catch (Exception e) {
            Log.e("EthereumManager", "Error getting block timestamp: " + e.getMessage());
            return "Unknown time";
        }
    }

    // Get event name from blockchain transaction
    private String getEventNameFromBlockchainData(String walletAddress, BigInteger tokenAmount, String timestamp) {
        try {
            int userId = mysqliteopenhelper.getUserIdFromWallet(walletAddress);
            if (userId == -999) {
                Log.w("EthereumManager", "Could not find user for wallet: " + walletAddress);
                return null;
            }
            // Convert token amount back to integer for SQLite comparison
            int amount = convertWeiToTokens(tokenAmount).intValue();
            
            // Parse timestamp for rough comparison (transactions might be a few seconds apart)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date blockchainDate = sdf.parse(timestamp);

            // Look for SQLite transaction with matching user, amount, and timestamp (within 5 minutes)
            Cursor cursor = mysqliteopenhelper.getUserTrans(userId);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String sqliteTimestamp = cursor.getString(1);
                    int sqliteAmount = cursor.getInt(4);
                    String transactionType = cursor.getString(6);
                    
                    // Only check event check-ins (type 'e')
                    if (transactionType.equals("e") && sqliteAmount == amount) {
                        try {
                            Date sqliteDate = sdf.parse(sqliteTimestamp);
                            long timeDiff = Math.abs(blockchainDate.getTime() - sqliteDate.getTime());
                            
                            // If timestamps are within 5 minutes (300,000 ms), consider it a match
                            if (timeDiff < 300000) {
                                int eventId = cursor.getInt(5);
                                String eventName = mysqliteopenhelper.getEventName(eventId);
                                Log.d("EthereumManager", "Found matching event: " + eventName + " (amount: " + amount + ")");
                                cursor.close();
                                return eventName;
                            }
                        } catch (Exception e) {
                            Log.w("EthereumManager", "Error parsing SQLite timestamp: " + sqliteTimestamp);
                        }
                    }
                }
                cursor.close();
            }
            Log.w("EthereumManager", "No matching SQLite event found for amount " + amount + " at " + timestamp);
            return null;
        } catch (Exception e) {
            Log.e("EthereumManager", "Error getting event name from SQLite: " + e.getMessage());
            return null;
        }
    }

    public static class BlockchainTransaction {
        public BigInteger blockNumber;
        public String timestamp;
        public String description;
        public String amount;
        public String type;
        
        public BlockchainTransaction(BigInteger blockNumber, String timestamp, String description, String amount, String type) {
            this.blockNumber = blockNumber;
            this.timestamp = timestamp;
            this.description = description;
            this.amount = amount;
            this.type = type;
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


/* test blockchain connection + examples

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
