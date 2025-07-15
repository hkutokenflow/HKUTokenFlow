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
    private static final String CONTRACT_ADDRESS = BlockchainConfig.TOKEN_CONTRACT_ADDRESS;
    private static final long CHAIN_ID = 1234;

    private final Web3j web3j;
    private final Admin admin;
    private final ContractGasProvider gasProvider;
    private final Mysqliteopenhelper mysqliteopenhelper;
    private final Context context;
    private Sc_test contract;
    private TransactionManager transactionManager;
    private Credentials adminCredentials;

    private boolean isInitialized = false;

    private BigInteger tokenDecimals = null;

    public EthereumManager(Context context) {
        this.context = context;
        this.mysqliteopenhelper = new Mysqliteopenhelper(context);

        HttpService httpService = new HttpService(BLOCKCHAIN_URL);
        web3j = Web3j.build(httpService);
        admin = Admin.build(httpService); 

        gasProvider = new ContractGasProvider() {
            @Override
            public BigInteger getGasPrice(String contractFunc) {
                return BigInteger.valueOf(20_000_000_000L); // 20 Gwei
            }
    
            @Override
            public BigInteger getGasPrice() {
                return BigInteger.valueOf(20_000_000_000L); // 20 Gwei
            }
    
            @Override
            public BigInteger getGasLimit(String contractFunc) {
                // ✅ MUCH LOWER gas limit for minting
                if ("mintTokens".equals(contractFunc)) {
                    return BigInteger.valueOf(300_000L); // 300k gas for minting
                }
                return BigInteger.valueOf(500_000L); // 500k gas for other functions
            }
    
            @Override
            public BigInteger getGasLimit() {
                return BigInteger.valueOf(500_000L); // Default 500k gas
            }
        };
        
        Log.d("Ethereum Manager", "EthereumManager created - call initializeSecurely() before use");
    }

    // Initialize EthereumManager with secure Android Keystore credentials
    public boolean initializeSecurely() { 
        try {
            Log.d("Ethereum Manager", "--- Starting Ethereum Manager initialization ---");
            
            // Load credentials from Android Keystore (faster than SCrypt, prevent out of memory error)
            Log.d("Ethereum Manager", "Loading secure credentials...");
            adminCredentials = SecurePrivateKeyManager.loadSecureCredentials(context);
            if (adminCredentials == null) {
                Log.e("Ethereum Manager", "Failed to load secure credentials");
                return false;
            }
            Log.d("Ethereum Manager", "> Credentials loaded successfully");

            // Create transaction manager with loaded credentials
            Log.d("Ethereum Manager", "Creating transaction manager...");
            transactionManager = new RawTransactionManager(web3j, adminCredentials, CHAIN_ID);
            Log.d("Ethereum Manager", "> Transaction manager created");

            // Load smart contract with admin credentials
            Log.d("Ethereum Manager", "Loading smart contract at: " + CONTRACT_ADDRESS);
            contract = Sc_test.load(
                    CONTRACT_ADDRESS,
                    web3j,
                    transactionManager,
                    gasProvider
            );
            Log.d("Ethereum Manager", "> Smart contract loaded");

            Log.d("Ethereum Manager", "Loading token decimals...");
            loadTokenDecimals();
            Log.d("Ethereum Manager", "> Token decimals loaded");

            isInitialized = true;
            Log.d("Ethereum Manager", "Successfully initialized with secure credentials");
            Log.d("Ethereum Manager", "Admin address: " + adminCredentials.getAddress());
            return true;

        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error initializing with secure credentials: " + e.getMessage());
            return false;
        }
    }

    // Check if EthereumManager is initialized
    public boolean isInitialized() {
        return isInitialized && adminCredentials != null && transactionManager != null && contract != null;
    }

    // ------------------------ Wallet interactions ------------------------

    // Mint tokens to wallet address
    public boolean mintTokens(String toAddress, BigInteger amount) {
        Log.d("Ethereum Manager", "=== mintTokens() called ===");
        if (!isInitialized()) {
            Log.e("Ethereum Manager", "EthereumManager not initialized. Call initializeWithPassword() first.");
            throw new IllegalStateException("EthereumManager not initialized. Call initializeWithPassword() first.");
        }
        
        boolean manualMiningStart = false;
        try {                
            long startTime = System.currentTimeMillis();
            BigInteger wei = convertTokensToWei(amount);

            if (!isMiningActive()) {
                boolean startSuccess = GethMiningController.startMining(4).get();
                if (!startSuccess) {
                    Log.e("Ethereum Manager", "Failed to start mining");
                    return false;
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
            return true; // success

        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error minting tokens: " + e.getMessage());
            return false;
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

    // Transfer tokens (redeem)
    public void transferTokens(String fromAddress, String toAddress, BigInteger amount) {
        Log.d("Ethereum Manager", "=== transferTokens() called ===");
        Log.d("Ethereum Manager", "From: " + fromAddress);
        Log.d("Ethereum Manager", "To: " + toAddress);
        Log.d("Ethereum Manager", "Amount: " + amount + " tokens");

        if (!isInitialized()) {
            Log.e("Ethereum Manager", "EthereumManager not initialized. Call initializeSecurely() first.");
            throw new IllegalStateException("EthereumManager not initialized. Call initializeSecurely() first.");
        }

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

            TransactionReceipt receipt = contract.redeemTokens(fromAddress, toAddress, wei).send();
            long endTime = System.currentTimeMillis();
            Log.d("Ethereum Manager", "Tokens transferred successfully: " + receipt.getTransactionHash());
            Log.d("Ethereum Manager", "From: " + fromAddress + " To: " + toAddress + " Amount: " + amount);
            Log.d("Ethereum Manager", "Transaction mined in " + (endTime - startTime) + "ms");
            Log.d("Ethereum Manager", "Transaction hash: " + receipt.getTransactionHash());
            Log.d("Ethereum Manager", "Block number: " + receipt.getBlockNumber());
            Log.d("Ethereum Manager", "Gas used: " + receipt.getGasUsed());

        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error transferring tokens: " + e.getMessage());
            throw new RuntimeException("Failed to transfer tokens: " + e.getMessage(), e);
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

    // ------------------------ Get wallet data ------------------------

    // Get wallet balance
    public BigInteger getBalance(String address) {
        Log.d("Ethereum Manager", "=== getBalance() called for address: " + address + "===");
        Log.d("Ethereum Manager", "Current initialization status:");
        Log.d("Ethereum Manager", "  isInitialized flag: " + isInitialized);
        Log.d("Ethereum Manager", "  adminCredentials: " + (adminCredentials != null ? "OK" : "NULL"));
        Log.d("Ethereum Manager", "  transactionManager: " + (transactionManager != null ? "OK" : "NULL"));
        Log.d("Ethereum Manager", "  contract: " + (contract != null ? "OK" : "NULL"));
        Log.d("Ethereum Manager", "  EthereumManager instance: " + this.hashCode());
        
        if (!isInitialized()) {
            Log.e("Ethereum Manager", "EthereumManager not initialized. Call initializeSecurely() first.");
            return BigInteger.ZERO;
        }

        if (address == null || address.isEmpty()) {
            Log.e("Ethereum Manager", "Address is null or empty");
            return BigInteger.ZERO;
        }
        
        if (!address.startsWith("0x") || address.length() != 42) {
            Log.e("Ethereum Manager", "Invalid address format: " + address);
            return BigInteger.ZERO;
        }

        try {
            BigInteger tokenUnits = contract.balanceOf(address).send();
            if (tokenUnits == null) {
                Log.e("Ethereum Manager", "Contract returned null balance");
                return BigInteger.ZERO;
            }

            BigInteger tokenBalance = convertWeiToTokens(tokenUnits);
            return tokenBalance;
            
        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error getting balance: " + e.getMessage());
            Log.e("Ethereum Manager", "Exception type: " + e.getClass().getSimpleName());
            Log.e("Ethereum Manager", "Exception details: ", e);
            return BigInteger.ZERO;
        }
    }

    // Get user wallet transaction records
    public List<BlockchainTransaction> getWalletTransactionHistory(String walletAddress) {
        List<BlockchainTransaction> transactions = new ArrayList<>();

        if (!isInitialized()) {
            Log.e("Ethereum Manager", "EthereumManager not initialized. Call initializeSecurely() first.");
            return transactions;
        }
        
        Log.d("EthereumManager", "=== Starting getWalletTransactionHistory ===");
        Log.d("EthereumManager", "Wallet address: " + walletAddress);
        
        try {           
            // ===== Get all transfer events to this address (receiver = address, includes minting) =====
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

                // ----- minting (from zero address) -----
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
                
                // ----- Regular transfers to address (vendors: tokens received) -----
                } else {
                    String rewardName = getRewardNameFromTransfer(event.from, event.to, event.value, timestamp);
                    String studentUsername = getStudentUsernameFromWallet(event.from);
                    String description;
                    if (rewardName != null) {
                        description = rewardName;
                        Log.d("EthereumManager", "  - reward redemption: " + rewardName + " by " + studentUsername);
                    } else {
                        description = "Transfer Received";
                        Log.d("EthereumManager", "  - regular transfer from " + studentUsername);
                    }
                    
                    BlockchainTransaction tx = new BlockchainTransaction(
                        event.log.getBlockNumber(),
                        timestamp,
                        description,
                        "+" + convertWeiToTokens(event.value).toString(),
                        "TRANSFER_IN",
                        rewardName, 
                        studentUsername, 
                        event.from,
                        event.to
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

                String rewardName = getRewardNameFromTransfer(walletAddress, event.to, event.value, timestamp);
                String description;
                if (rewardName != null) {
                    description = "Redeemed: " + rewardName;
                    Log.d("EthereumManager", "  - reward redemption: " + rewardName);
                } else {
                    // Regular transfer
                    description = "Transfer";
                    Log.d("EthereumManager", "  - regular transfer");
                }

                BlockchainTransaction tx = new BlockchainTransaction(
                    event.log.getBlockNumber(),
                    timestamp,
                    description,
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
            
            // Parse timestamp for comparison 
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date blockchainDate = sdf.parse(timestamp);

            String bestEventName = null;
            long smallestTimeDiff = Long.MAX_VALUE;

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
                            if (timeDiff < 300000 && timeDiff < smallestTimeDiff) {
                                smallestTimeDiff = timeDiff;
                                int eventId = cursor.getInt(5);
                                bestEventName = mysqliteopenhelper.getEventName(eventId);
                                Log.d("EthereumManager", "Found closer matching event: " + bestEventName + " (amount: " + amount + ")");
                            }
                        } catch (Exception e) {
                            Log.w("EthereumManager", "Error parsing SQLite timestamp: " + sqliteTimestamp);
                        }
                    }
                }
                cursor.close();
            }

            if (bestEventName != null) {
                Log.d("EthereumManager", "Best matching event: " + bestEventName + " (smallest time diff: " + smallestTimeDiff + "ms)");
                return bestEventName;
            }

            Log.w("EthereumManager", "No matching SQLite event found for amount " + amount + " at " + timestamp);
            return null;
        } catch (Exception e) {
            Log.e("EthereumManager", "Error getting event name from SQLite: " + e.getMessage());
            return null;
        }
    }

    private String getRewardNameFromTransfer(String fromAddress, String toAddress, BigInteger tokenAmount, String timestamp) {
        try {
            int studentId = mysqliteopenhelper.getUserIdFromWallet(fromAddress);
            if (studentId == -999) {
                Log.w("EthereumManager", "Could not find student for wallet: " + fromAddress);
                return null;
            }
            int vendorId = mysqliteopenhelper.getUserIdFromWallet(toAddress);
            if (vendorId == -999) {
                Log.w("EthereumManager", "Could not find vendor for wallet: " + toAddress);
                return null;
            }

            int amount = convertWeiToTokens(tokenAmount).intValue();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date blockchainDate = sdf.parse(timestamp);

            Cursor cursor = mysqliteopenhelper.getStudentRewards(studentId);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int rewardId = cursor.getInt(2);  // get reward id from student_rewards record
                    Cursor rewardCursor = mysqliteopenhelper.getRewardFromId(rewardId);
                    if (rewardCursor != null && rewardCursor.moveToFirst()) {
                        int rewardValue = rewardCursor.getInt(3);
                        int rewardVendorId = rewardCursor.getInt(4);
                        if (rewardValue == amount && rewardVendorId == vendorId) {
                            String rewardName = mysqliteopenhelper.getRewardName(rewardId);
                            Log.d("EthereumManager", "Found matching reward: " + rewardName + " (amount: " + amount + ", vendor: " + vendorId + ")");
                            rewardCursor.close();
                            cursor.close();
                            return rewardName;
                        }
                        rewardCursor.close();
                    }
                }
                cursor.close();
            }
            Log.w("EthereumManager", "No matching SQLite reward found for amount " + amount + " from student " + studentId + " to vendor " + vendorId + " at " + timestamp);
            return null;
        } catch (Exception e) {
            Log.e("EthereumManager", "Error getting reward name from transfer: " + e.getMessage());
            return null;
        }
    }

    private String getStudentUsernameFromWallet(String walletAddress) {
        try {
            int userId = mysqliteopenhelper.getUserIdFromWallet(walletAddress);
            if (userId == -999) {
                Log.w("EthereumManager", "Could not find user for wallet: " + walletAddress);
                return "Unknown User";
            }
            
            Cursor cursor = mysqliteopenhelper.getReadableDatabase().query(
                "Users", 
                new String[]{"username"}, 
                "_id = ?", 
                new String[]{String.valueOf(userId)}, 
                null, null, null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                String username = cursor.getString(0);
                cursor.close();
                return username;
            }
            if (cursor != null) {
                cursor.close();
            }
            
            return "Unknown User";
        } catch (Exception e) {
            Log.e("EthereumManager", "Error getting username from wallet: " + e.getMessage());
            return "Unknown User";
        }
    }

    public int getTotalTransactionCount() {
        Log.d("Ethereum Manager", "=== Getting total transaction count ===");
        if (!isInitialized()) {
            Log.e("Ethereum Manager", "EthereumManager not initialized. Call initializeSecurely() first.");
            return 0;
        }
        try {
            EthFilter filter = new EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            CONTRACT_ADDRESS);

            // Add Transfer event topic
            filter.addSingleTopic(org.web3j.abi.EventEncoder.encode(Sc_test.TRANSFER_EVENT));
            
            // Get all logs matching the filter
            EthLog ethLog = web3j.ethGetLogs(filter).send();
            List<org.web3j.protocol.core.methods.response.EthLog.LogResult> logs = ethLog.getLogs();

            int totalTransactions = logs.size() - 1; // don't count initial minting to admin
            Log.d("Ethereum Manager", "Total Transfer events found: " + totalTransactions);
            return totalTransactions; 

        } catch (Exception e) {
            Log.e("Ethereum Manager", "Error getting total transaction count: " + e.getMessage());
            return 0;
        }
    }

    // admin recent transactions fragment
    public List<BlockchainTransaction> getAllTransactions() {
        List<BlockchainTransaction> allTransactions = new ArrayList<>();
        if (!isInitialized()) {
            Log.e("Ethereum Manager", "EthereumManager not initialized. Call initializeSecurely() first.");
            return allTransactions;
        }

        try {
            Log.d("EthereumManager", "=== Getting all transactions ===");
            // Create filter for all Transfer events
            EthFilter filter = new EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            CONTRACT_ADDRESS
            );
            // Add Transfer event topic
            filter.addSingleTopic(org.web3j.abi.EventEncoder.encode(Sc_test.TRANSFER_EVENT));
            
            // Get all logs matching the filter
            EthLog ethLog = web3j.ethGetLogs(filter).send();
            List<org.web3j.protocol.core.methods.response.EthLog.LogResult> logs = ethLog.getLogs();
            Log.d("EthereumManager", "Found " + logs.size() + " Transfer events");

            boolean isFirst = true;
            for (EthLog.LogResult logResult : logs) {
                if (isFirst) {
                    Log.d("EthereumManager", "Skipping initial minting");
                    isFirst = false;
                    continue;
                }
                
                org.web3j.protocol.core.methods.response.Log log = (org.web3j.protocol.core.methods.response.Log) logResult.get();
                Sc_test.TransferEventResponse event = Sc_test.getTransferEventFromLog(log);
                
                String timestamp = getBlockTimestamp(event.log.getBlockNumber());
                String amount = convertWeiToTokens(event.value).toString();
                
                // Determine transaction type and description
                String description;
                String type;
                if (event.from.equals("0x0000000000000000000000000000000000000000")) {
                    // Minting transaction
                    description = "Token Minting";
                    type = "MINT";
                } else {
                    // Regular transfer
                    description = "Token Transfer";
                    type = "TRANSFER";
                }
                BlockchainTransaction tx = new BlockchainTransaction(
                    event.log.getBlockNumber(),
                    timestamp,
                    description,
                    amount,
                    type,
                    null,
                    null, 
                    event.from,
                    event.to
                );
                allTransactions.add(tx);
            }
            allTransactions.sort((a, b) -> b.blockNumber.compareTo(a.blockNumber));
            Log.d("EthereumManager", "Processed " + allTransactions.size() + " transactions");

        } catch (Exception e) {
            Log.e("EthereumManager", "Error getting all transactions: " + e.getMessage());
            Log.e("EthereumManager", "Exception details: ", e);
        }

        return allTransactions;
    }

    public static class BlockchainTransaction {
        public BigInteger blockNumber;
        public String timestamp;
        public String description;
        public String amount;
        public String type;
        public String rewardName;      
        public String studentUsername; 
        public String fromAddress;     
        public String toAddress; 
        
        public BlockchainTransaction(BigInteger blockNumber, String timestamp, String description, String amount, String type) {
            this.blockNumber = blockNumber;
            this.timestamp = timestamp;
            this.description = description;
            this.amount = amount;
            this.type = type;
            this.rewardName = null;
            this.studentUsername = null;
            this.fromAddress = null;
            this.toAddress = null;
        }

        public BlockchainTransaction(BigInteger blockNumber, String timestamp, String description, String amount, String type, String rewardName, String studentUsername, String fromAddress, String toAddress) {
            this.blockNumber = blockNumber;
            this.timestamp = timestamp;
            this.description = description;
            this.amount = amount;
            this.type = type;
            this.rewardName = rewardName;
            this.studentUsername = studentUsername;
            this.fromAddress = fromAddress;
            this.toAddress = toAddress;
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
            if (contract == null) {
                Log.w("Ethereum Manager", "Contract is null, cannot load token decimals");
                tokenDecimals = BigInteger.valueOf(18); // Default to 18 decimals
                return;
            }
            
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

    public boolean testNetworkConnection() {
        try {
            Log.d("Ethereum Manager", "Testing network connection to: " + BLOCKCHAIN_URL);
            
            // Test basic connectivity
            BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            Log.d("Ethereum Manager", "Network connected. Latest block: " + blockNumber);
            
            // Test if we can get chain ID
            BigInteger chainId = web3j.ethChainId().send().getChainId();
            Log.d("Ethereum Manager", "Chain ID: " + chainId);
            
            return true;
        } catch (Exception e) {
            Log.e("Ethereum Manager", "Network connection failed: " + e.getMessage());
            return false;
        }
    }

    public boolean isContractWorking() {
        if (!isInitialized()) {
            Log.e("Ethereum Manager", "Cannot check contract - not initialized");
            return false;
        }

        if (!testNetworkConnection()) {
            Log.e("Ethereum Manager", "Network connection failed");
            return false;
        }
        
        try {
            String name = contract.name().send();
            Log.d("Ethereum Manager", "Contract name: " + name);
            
            BigInteger decimals = contract.decimals().send();
            Log.d("Ethereum Manager", "Contract decimals: " + decimals);
            
            BigInteger totalSupply = contract.totalSupply().send();
            Log.d("Ethereum Manager", "Contract total supply: " + totalSupply);
            
            return true;
        } catch (Exception e) {
            Log.e("Ethereum Manager", "Contract is not working: " + e.getMessage());
            Log.e("Ethereum Manager", "Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            return false;
        }
    }


    public void checkContractState() {
        if (!isInitialized()) {
            Log.e("Ethereum Manager", "Cannot check contract state - not initialized");
            return;
        }
        
        new Thread(() -> {
            try {
                Log.d("Ethereum Manager", "=== Checking Contract State ===");
                
                // Check total supply
                BigInteger totalSupply = contract.totalSupply().send();
                Log.d("Ethereum Manager", "Total token supply: " + convertWeiToTokens(totalSupply) + " tokens");
                
                // Check contract name and symbol
                String name = contract.name().send();
                String symbol = contract.symbol().send();
                Log.d("Ethereum Manager", "Token name: " + name);
                Log.d("Ethereum Manager", "Token symbol: " + symbol);
                
                // Check admin balance
                BigInteger adminBalance = getBalance(adminCredentials.getAddress());
                Log.d("Ethereum Manager", "Admin balance: " + adminBalance + " " + symbol);
                
            } catch (Exception e) {
                Log.e("Ethereum Manager", "Error checking contract state: " + e.getMessage());
            }
        }).start();
    }

    public Sc_test getContract() {
        return contract;
    }

    public void testContractAccess(String oldContractAddress) {
        new Thread(() -> {
            try {
                Log.d("Ethereum Manager", "=== TESTING CONTRACT ACCESS ===");
                Log.d("Ethereum Manager", "Old contract address: " + oldContractAddress);
                Log.d("Ethereum Manager", "Current contract address: " + CONTRACT_ADDRESS);

                String code = web3j.ethGetCode(oldContractAddress, DefaultBlockParameterName.LATEST).send().getCode();
                Log.d("Ethereum Manager", "Contract code at old address: " + (code.equals("0x") ? "NO CONTRACT" : "CONTRACT EXISTS"));
                Log.d("Ethereum Manager", "Code length: " + code.length());

                if (code.equals("0x") || code.equals("0x0")) {
                    Log.e("Ethereum Manager", "No contract found at address");
                    return;
                }
                else {
                    Log.d("Ethereum Manager", "Contract found at address");
                }
            } catch (Exception e) {
                Log.e("Ethereum Manager", "Error testing old contract: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
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
