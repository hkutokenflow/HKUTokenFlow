package com.example.workshop1.Student.StudentHome;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.workshop1.Admin.RecentTransaction.RecentTransactionsFragment;
import com.example.workshop1.Admin.Vendor.VendorItem;
import com.example.workshop1.Ethereum.BlockchainConfig;
import com.example.workshop1.Ethereum.EthereumManager;
import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class StudentHomeFragment extends Fragment {

    private TextView studentWalletText;

    private TableLayout transactionsTable;
    private EditText searchEditText;
    private List<Transaction> allTransactions = new ArrayList<>();
    private Mysqliteopenhelper mysqliteopenhelper;
    private EthereumManager ethereumManager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_student_home, container, false);

        mysqliteopenhelper = new Mysqliteopenhelper(getContext());
        ethereumManager = new EthereumManager(getContext());

        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");

        // ---------- wallet balance ----------
        studentWalletText = root.findViewById(R.id.student_wallet_balance);


        // Initialize in background thread to avoid OOM
        new Thread(() -> {
            try {
                boolean initialized = ethereumManager.initializeSecurely();
                
                if (initialized && thisUser != null) {
                    Log.d("StudentHome", "Initialization successful, loading blockchain data...");
                    loadBlockchainData(thisUser);
                    
                } else {
                    Log.e("StudentHome", "Failed to initialize EthereumManager");
                    requireActivity().runOnUiThread(() -> {;
                        studentWalletText.setText("Blockchain unavailable");
                        loadSQLiteTransactions(thisUser);
                    });
                }
                
            } catch (OutOfMemoryError e) {
                Log.e("StudentHome", "OutOfMemoryError during initialization: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    studentWalletText.setText("Blockchain unavailable");
                    loadSQLiteTransactions(thisUser);
                });
            }
        }).start();

        // ---------- transaction table ----------
        transactionsTable = root.findViewById(R.id.recent_transactions_table);
        searchEditText = root.findViewById(R.id.transaction_search);

        // setupDummyData(); // SQLite
        displayTransactions(allTransactions);

        // Search function
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTransactions(s.toString());
            }
        });

        return root;
    }

    private void loadBlockchainData(User thisUser) {
        if (thisUser == null || thisUser.getWallet() == null) {
            Log.e("StudentHome", "No user data");
            loadSQLiteTransactions(thisUser);
            return;
        }

        // Load wallet balance
        try {
            Log.d("StudentHome", "=== LOADING BLOCKCHAIN DATA ===");
            Log.d("StudentHome", "Current user: " + thisUser.getUsername());
            Log.d("StudentHome", "Current wallet address: " + thisUser.getWallet());
            Log.d("StudentHome", "User type: " + thisUser.getType());
            
            if (!ethereumManager.isContractWorking()) {
                Log.e("StudentHome", "Contract is not working, falling back to SQLite");
                requireActivity().runOnUiThread(() -> {
                    studentWalletText.setText("Blockchain unavailable");
                    loadSQLiteTransactions(thisUser);
                });
                return;
            }

            BigInteger currentBalance = ethereumManager.getBalance(thisUser.getWallet());
            Log.d("StudentHome", "Balance for " + thisUser.getWallet() + ": " + currentBalance);
            
            requireActivity().runOnUiThread(() -> {
                String displayBalance = currentBalance + " HKUT";
                studentWalletText.setText(displayBalance);
            });
            
            Log.d("StudentHome", "> Balance loaded: " + currentBalance);

            ethereumManager.checkContractState();
    
        } catch (Exception e) {
            Log.e("StudentHome", "Error getting balance: " + e.getMessage());
            requireActivity().runOnUiThread(() -> {
                studentWalletText.setText("Error loading balance");
            });
        }

        // Load transaction history
        try {
            Log.d("StudentHome", "Loading transaction history...");
            List<EthereumManager.BlockchainTransaction> transactions = 
                ethereumManager.getWalletTransactionHistory(thisUser.getWallet());
            
            // Update UI from background thread
            requireActivity().runOnUiThread(() -> {
                allTransactions.clear();
                for (EthereumManager.BlockchainTransaction tx : transactions) {
                    allTransactions.add(new Transaction(tx.timestamp, tx.description, tx.amount));
                }
                displayTransactions(allTransactions);
            });
            
            Log.d("StudentHome", "> Transactions loaded: " + transactions.size() + " items");
    
        } catch (Exception e) {
            Log.e("StudentHome", "Error loading blockchain transactions: " + e.getMessage());
            requireActivity().runOnUiThread(() -> {
                loadSQLiteTransactions(thisUser);
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh blockchain balance when fragment resumes
        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");
        if (thisUser != null && ethereumManager != null) {
            if (thisUser != null) {
                Log.d("StudentHome", "onResume - User: " + thisUser.getUsername());
                Log.d("StudentHome", "onResume - Wallet: " + thisUser.getWallet());
            } else {
                Log.e("StudentHome", "onResume - User is NULL!");
            }
            
            new Thread(() -> {
                try {
                    // Wait for initialization to complete
                    int maxWaitTime = 5000; // Wait up to 5 seconds
                    int checkInterval = 100; // Check every 100ms
                    int totalWaited = 0;
                    while (!ethereumManager.isInitialized() && totalWaited < maxWaitTime) {
                        Thread.sleep(checkInterval);
                        totalWaited += checkInterval;
                    }
                    if (!ethereumManager.isInitialized()) {
                        Log.w("StudentHome", "EthereumManager still not initialized after " + maxWaitTime + "ms, skipping balance refresh");
                        return;
                    }

                    // Refresh balance
                    BigInteger currentBalance = ethereumManager.getBalance(thisUser.getWallet());
                    requireActivity().runOnUiThread(() -> {
                        String displayBalance = currentBalance + " HKUT";
                        studentWalletText.setText(displayBalance);
                    });

                    // Refresh transaction history
                    List<EthereumManager.BlockchainTransaction> transactions =
                            ethereumManager.getWalletTransactionHistory(thisUser.getWallet());

                    requireActivity().runOnUiThread(() -> {
                        allTransactions.clear();
                        for (EthereumManager.BlockchainTransaction tx : transactions) {
                            allTransactions.add(new Transaction(tx.timestamp, tx.description, tx.amount));
                        }
                        displayTransactions(allTransactions);
                    });

                } catch (Exception e) {
                    Log.e("StudentHome", "Error refreshing balance: " + e.getMessage());
                    requireActivity().runOnUiThread(() -> {
                        studentWalletText.setText("Error loading balance");
                    });
                }
            }).start();
        }
    }

    private void displayTransactions(List<Transaction> transactions) {
        // 先移除旧数据行（保留表头）
        int childCount = transactionsTable.getChildCount();
        if (childCount > 1) {
            transactionsTable.removeViews(1, childCount - 1);
        }

        int index = 0;
        for (Transaction t : transactions) {
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            // 可选：交替背景色
            if (index % 2 == 0) {
                row.setBackgroundColor(Color.parseColor("#F9F9F9")); // 浅灰
            }

            row.addView(createCell(t.date));
            row.addView(createCell(t.event));
            row.addView(createCell(t.balance));

            transactionsTable.addView(row);
            index++;
        }
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextSize(15); // 稍微大一点
        tv.setPadding(8, 24, 8, 24); // 上下 padding 增加
        tv.setGravity(android.view.Gravity.CENTER); // 居中对齐
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, 1f); // 每格平分
        tv.setLayoutParams(params);
        return tv;
    }


    private void filterTransactions(String query) {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : allTransactions) {
            if (t.matches(query)) {
                filtered.add(t);
            }
        }
        displayTransactions(filtered);
    }

    // 内部类：交易模型
    static class Transaction {
        String date, event, balance;

        Transaction(String date, String event, String balance) {
            this.date = date;
            this.event = event;
            this.balance = balance;
        }

        boolean matches(String query) {
            String lower = query.toLowerCase();
            return date.toLowerCase().contains(lower) ||
                    event.toLowerCase().contains(lower) ||
                    balance.toLowerCase().contains(lower);
        }
    }

    private void loadSQLiteTransactions(User thisUser) {
        int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
        Cursor cursor = mysqliteopenhelper.getUserTrans(uid);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String datetime = cursor.getString(1);
                Log.d("StudentHome", "datetime: " + datetime);
                int src = cursor.getInt(2);
                int amt = cursor.getInt(4);

                // determine +ve or -ve amt (-ve if source is student, ie student pay)
                if (src == uid) { amt = -amt; }

                // get event / reward name
                String event;
                if (cursor.getString(6).equals("e")) {
                    int eid = cursor.getInt(5);
                    event = mysqliteopenhelper.getEventName(eid);
                } else if (cursor.getString(6).equals("r")) {
                    int rid = cursor.getInt(5);
                    event = mysqliteopenhelper.getRewardName(rid);
                } else { event = "Invalid"; }

                allTransactions.add(new Transaction(datetime, event, String.valueOf(amt)));
            }
        }
    }
}






