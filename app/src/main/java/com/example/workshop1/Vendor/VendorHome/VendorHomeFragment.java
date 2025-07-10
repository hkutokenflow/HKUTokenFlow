package com.example.workshop1.Vendor.VendorHome;

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
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.workshop1.Admin.RecentTransaction.RecentTransactionsFragment;
import com.example.workshop1.Ethereum.EthereumManager;
import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.User;
import com.example.workshop1.Student.StudentHome.StudentHomeFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VendorHomeFragment extends Fragment {

    private TextView vendorWalletText;


    private LineChart vendorTransactionsChart;
    private Spinner chartTimeRangeSpinner;
    private String selectedRange = "Weekly";
    private TableLayout transactionsTable;
    private EditText searchEditText;
    private List<Transaction> allTransactions = new ArrayList<>();
    private Mysqliteopenhelper mysqliteopenhelper;
    private EthereumManager ethereumManager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_vendor_home, container, false);

        mysqliteopenhelper = new Mysqliteopenhelper(getContext());
        ethereumManager = new EthereumManager(getContext());
        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");

        // ---------- wallet balance ----------
        vendorWalletText = root.findViewById(R.id.vendor_wallet_balance);

        new Thread(() -> {
            try {
                boolean initialized = ethereumManager.initializeSecurely();
                if (initialized && thisUser != null) {
                    Log.d("VendorHome", "Initialization successful, loading blockchain data");
                    loadBlockchainData(thisUser);
                } else {
                    Log.e("VendorHome", "Failed to initialize EthereumManager");
                    requireActivity().runOnUiThread(() -> {
                        vendorWalletText.setText("Blockchain unavailable");
                        loadSQLiteTransactions(thisUser);
                    });
                }
            } catch (OutOfMemoryError e) {
                Log.e("VendorHome", "OutOfMemoryError during initialization: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    vendorWalletText.setText("Blockchain unavailable");
                    loadSQLiteTransactions(thisUser);
                });
            }
        }).start();


        if (thisUser != null) {
            int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
            int currentBalance = mysqliteopenhelper.getUserBalance(uid);
            vendorWalletText.setText(String.valueOf(currentBalance));
        } else {
            vendorWalletText.setText("Balance not available");
        }

        //barchart
        vendorTransactionsChart = root.findViewById(R.id.transactions_chart);
        chartTimeRangeSpinner = root.findViewById(R.id.time_range_spinner);

        chartTimeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRange = parent.getItemAtPosition(position).toString();
                setupTransactionsChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        setupTransactionsChart();

        //transaction table
        transactionsTable = root.findViewById(R.id.recent_transactions_table);
        searchEditText = root.findViewById(R.id.transaction_search);

        int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
        Cursor cursor = mysqliteopenhelper.getUserTrans(uid);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String datetime = cursor.getString(1);
                int src = cursor.getInt(2);
                // int dest = cursor.getInt(3);  // vendor must be dest
                int amt = cursor.getInt(4);  // amt must be +ve (receive tokens)
                int rid = cursor.getInt(5);
                String reward = mysqliteopenhelper.getRewardName(rid);

                allTransactions.add(new Transaction(datetime, reward, String.valueOf(src), String.valueOf(amt)));
            }
        }


        displayTransactions(allTransactions);

        // 搜索功能
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
            Log.e("VendorHome", "No user data");
            loadSQLiteTransactions(thisUser);
            return;
        }
        // Load wallet balance
        try {
            Log.d("VendorHome", "=== LOADING BLOCKCHAIN DATA ===");
            Log.d("VendorHome", "Current user: " + thisUser.getUsername());
            Log.d("VendorHome", "Current wallet address: " + thisUser.getWallet());
            Log.d("VendorHome", "User type: " + thisUser.getType());

            if (!ethereumManager.isContractWorking()) {
                Log.e("VendorHome", "Contract is not working, falling back to SQLite");
                requireActivity().runOnUiThread(() -> {
                    vendorWalletText.setText("Blockchain unavailable");
                    loadSQLiteTransactions(thisUser);
                });
                return;
            }

            BigInteger currentBalance = ethereumManager.getBalance(thisUser.getWallet());
            Log.d("VendorHome", "Balance for " + thisUser.getWallet() + ": " + currentBalance);
            requireActivity().runOnUiThread(() -> {
                String displayBalance = currentBalance + " HKUT";
                vendorWalletText.setText(displayBalance);
            });
            Log.d("VendorHome", "> Balance loaded: " + currentBalance);
            ethereumManager.checkContractState();

        } catch (Exception e) {
            Log.e("VendorHome", "Error getting balance: " + e.getMessage());
            requireActivity().runOnUiThread(() -> {
                vendorWalletText.setText("Error loading balance");
            });
        }

        // Load transaction history
        try {
            Log.d("VendorHome", "Loading transaction history...");
            List<EthereumManager.BlockchainTransaction> transactions =
                    ethereumManager.getWalletTransactionHistory(thisUser.getWallet());

            requireActivity().runOnUiThread(() -> {
                allTransactions.clear();
                for (EthereumManager.BlockchainTransaction tx : transactions) {
                    if (tx.type.equals("TRANSFER_IN") || tx.type.equals("MINT")) {
                        String rewardName = tx.rewardName != null ? tx.rewardName : tx.description;
                        String studentUsername = tx.studentUsername != null ? tx.studentUsername : "Student";
                        allTransactions.add(new Transaction(tx.timestamp, rewardName, studentUsername, tx.amount));
                    }
                }
                displayTransactions(allTransactions);
            });

            Log.d("VendorHome", "> Transactions loaded: " + transactions.size() + " items");
        } catch (Exception e) {
            Log.e("VendorHome", "Error loading blockchain transactions: " + e.getMessage());
            requireActivity().runOnUiThread(() -> {
                loadSQLiteTransactions(thisUser);
            });
        }

    }

    //-----------------------------------linechart--------------------------------
    // 设置 Transactions 的折线图
    private void setupTransactionsChart() {
        ArrayList<Entry> entries = new ArrayList<>();

        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");
        if (thisUser != null) {
            int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
            Map<String, Integer> transactionStats = mysqliteopenhelper.getUserTransactionStats(uid, selectedRange);

            // 填充缺失的日期
            Map<String, Integer> filledStats = mysqliteopenhelper.fillMissingDates(transactionStats, selectedRange);

            int index = 0; // 从0开始，而不是1
            for (Map.Entry<String, Integer> entry : filledStats.entrySet()) {
                entries.add(new Entry(index, entry.getValue().floatValue()));
                index++;
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Transactions");
        dataSet.setColor(getResources().getColor(R.color.blue_200));
        dataSet.setLineWidth(2f);
        dataSet.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark));
        LineData lineData = new LineData(dataSet);

        vendorTransactionsChart.setData(lineData);
        configureLineChart(vendorTransactionsChart);

        // 设置X轴标签
        if (selectedRange.equals("Monthly")) {
            setupMonthlyXAxis(vendorTransactionsChart);
        } else {
            resetXAxis(vendorTransactionsChart);
        }
    }

    // 重置X轴为默认设置
    private void resetXAxis(LineChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        if (selectedRange.equals("Weekly")) {
            // 设置Weekly模式的日期标签
            final String[] weeklyLabels = mysqliteopenhelper.getWeeklyLabels();
            xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value; // 移除 -1，因为x值现在从0开始
                    if (index >= 0 && index < weeklyLabels.length) {
                        return weeklyLabels[index];
                    }
                    return "";
                }
            });
            xAxis.setLabelCount(7, true);
        } else {
            xAxis.setValueFormatter(null); // 清除自定义格式化器
        }

        chart.invalidate();
    }

    // 设置月度的X轴标签
    private void setupMonthlyXAxis(LineChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // 设置月份标签
        final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value; // 移除 -1，因为x值现在从0开始
                if (index >= 0 && index < months.length) {
                    return months[index];
                }
                return "";
            }
        });

        xAxis.setLabelCount(12, true);
        chart.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");
        if (thisUser != null && ethereumManager != null) {
            Log.d("VendorHome", "onResume - User: " + thisUser.getUsername());
            Log.d("VendorHome", "onResume - Wallet: " + thisUser.getWallet());

            new Thread(() -> {
                try {
                    int maxWaitTime = 5000; // Wait up to 5 seconds
                    int checkInterval = 100; // Check every 100ms
                    int totalWaited = 0;
                    while (!ethereumManager.isInitialized() && totalWaited < maxWaitTime) {
                        Thread.sleep(checkInterval);
                        totalWaited += checkInterval;
                    }
                    if (!ethereumManager.isInitialized()) {
                        Log.w("VendorHome", "EthereumManager still not initialized after " + maxWaitTime + "ms, skipping balance refresh");
                        return;
                    }

                    // Refresh balance
                    BigInteger currentBalance = ethereumManager.getBalance(thisUser.getWallet());
                    requireActivity().runOnUiThread(() -> {
                        String displayBalance = currentBalance + " HKUT";
                        vendorWalletText.setText(displayBalance);
                    });

                    // Refresh transaction history
                    List<EthereumManager.BlockchainTransaction> transactions =
                            ethereumManager.getWalletTransactionHistory(thisUser.getWallet());

                    requireActivity().runOnUiThread(() -> {
                        allTransactions.clear();
                        for (EthereumManager.BlockchainTransaction tx : transactions) {
                            if (tx.type.equals("TRANSFER_IN") || tx.type.equals("MINT")) {
                                String rewardName = tx.rewardName != null ? tx.rewardName : tx.description;
                                String studentUsername = tx.studentUsername != null ? tx.studentUsername : "Student";
                                allTransactions.add(new Transaction(tx.timestamp, rewardName, studentUsername, tx.amount));
                            }
                        }
                        displayTransactions(allTransactions);
                    });

                } catch (Exception e) {
                    Log.e("VendorHome", "Error refreshing balance: " + e.getMessage());
                    requireActivity().runOnUiThread(() -> {
                        vendorWalletText.setText("Error loading balance");
                    });
                }
            }).start();
        }
    }


    // 配置折线图
    private void configureLineChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawGridLines(true);

        chart.invalidate();  // 刷新图表
    }



    //-----------------------------------transaction--------------------------------
    // --------------------------------ADD:SQLite--------------------------------
    // 静态数据
    private void setupDummyData() {
        allTransactions.clear();
        allTransactions.add(new Transaction("2025-04-25", "V001", "User123", "$500.00"));
        allTransactions.add(new Transaction("2025-04-26", "V002", "User456", "$200.00"));
        allTransactions.add(new Transaction("2025-04-27", "V003", "Alice", "$350.00"));
        allTransactions.add(new Transaction("2025-04-28", "V004", "Bob", "$150.00"));
        allTransactions.add(new Transaction("2025-04-29", "V005", "Charlie", "$420.00"));
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
            row.addView(createCell(t.voucher));
            row.addView(createCell(t.user));
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

    private void loadSQLiteTransactions(User thisUser) {
        if (thisUser == null) {
            Log.e("VendorHome", "User is null, cannot load SQLite transactions");
            return;
        }

        int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
        Cursor cursor = mysqliteopenhelper.getUserTrans(uid);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String datetime = cursor.getString(1);
                int src = cursor.getInt(2);
                int amt = cursor.getInt(4);
                int rid = cursor.getInt(5);
                String reward = mysqliteopenhelper.getRewardName(rid);
                allTransactions.add(new Transaction(datetime, reward, String.valueOf(src), String.valueOf(amt)));
            }
        }
        cursor.close();
        displayTransactions(allTransactions);
    }

    // 内部类：交易模型
    static class Transaction {
        String date, voucher, user, balance;

        Transaction(String date, String voucher, String user, String balance) {
            this.date = date;
            this.voucher = voucher;
            this.user = user;
            this.balance = balance;
        }

        boolean matches(String query) {
            String lower = query.toLowerCase();
            return date.toLowerCase().contains(lower) ||
                    voucher.toLowerCase().contains(lower) ||
                    user.toLowerCase().contains(lower) ||
                    balance.toLowerCase().contains(lower);
        }
    }
}






