package com.example.workshop1.Admin.AdminHome;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.workshop1.Ethereum.BlockchainConfig;
import com.example.workshop1.Ethereum.EthereumManager;
import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.User;
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

public class AdminHomeFragment extends Fragment {

    private TextView totalTokensText;
    private TextView totalTransactionsText;

    private LineChart tokensMinedChart;
    private LineChart transactionsChart;
    private Spinner chartTimeRangeSpinner;
    private String selectedRange = "Weekly";
    private Mysqliteopenhelper mysqliteopenhelper;
    private EthereumManager ethereumManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_home, container, false);
        
        mysqliteopenhelper = new Mysqliteopenhelper(getContext());
        ethereumManager = new EthereumManager(getContext());
        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");

        totalTokensText = root.findViewById(R.id.total_tokens_value);
        totalTransactionsText = root.findViewById(R.id.total_transactions_value);

        // Get blockchain data
        new Thread(() -> {
            try {
                boolean initialized = ethereumManager.initializeSecurely();
                
                if (initialized) {
                    Log.d("AdminHome", "Initialization successful, loading blockchain data...");
                    loadBlockchainData();
                } else {
                    Log.e("AdminHome", "Failed to initialize EthereumManager");
                    requireActivity().runOnUiThread(() -> {
                        loadSQLiteData(thisUser);
                    });
                }
            } catch (OutOfMemoryError e) {
                Log.e("AdminHome", "OutOfMemoryError during initialization: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    loadSQLiteData(thisUser);
                });
            }
        }).start();

        tokensMinedChart = root.findViewById(R.id.tokens_mined_chart);
        transactionsChart = root.findViewById(R.id.transactions_chart);
        chartTimeRangeSpinner = root.findViewById(R.id.time_range_spinner);

        chartTimeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRange = parent.getItemAtPosition(position).toString();
                setupTokensMinedChart();
                setupTransactionsChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        setupTokensMinedChart();
        setupTransactionsChart();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");
        if (ethereumManager != null) {
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
                        Log.w("AdminHome", "EthereumManager still not initialized after " + maxWaitTime + "ms, skipping refresh");
                        return;
                    }
                    loadBlockchainData();
                } catch (Exception e) {
                    Log.e("AdminHome", "Error refreshing blockchain data: " + e.getMessage());
                    // Fallback to SQLite data if blockchain refresh fails
                    requireActivity().runOnUiThread(() -> {
                        loadSQLiteData(thisUser);
                    });
                }
            }).start();;
        }
    }

    private void loadBlockchainData() {
        try {
            Log.d("AdminHome", "=== LOADING BLOCKCHAIN DATA ===");

            String OLD_CONTRACT_ADDRESS = BlockchainConfig.TOKEN_CONTRACT_ADDRESS;
            ethereumManager.testContractAccess(OLD_CONTRACT_ADDRESS);
            
            if (!ethereumManager.isContractWorking()) {
                Log.e("AdminHome", "Contract is not working, falling back to SQLite");
                requireActivity().runOnUiThread(() -> {
                    User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");
                    loadSQLiteData(thisUser);
                });
                return;
            }

            // Get total tokens minted 
            BigInteger totalSupply = ethereumManager.getContract().totalSupply().send();
            BigInteger totalTokensMinted = ethereumManager.convertWeiToTokens(totalSupply);
            // BigInteger initialMinting = BigInteger.valueOf(1_000_000); // initally minted 1000000 tokens to admin
            // BigInteger actualTokensMinted = totalTokensMinted.subtract(initialMinting);
            Log.d("AdminHome", "Total tokens minted: " + totalTokensMinted);

            // Get total no of transactions
            List<EthereumManager.BlockchainTransaction> allTransactions = ethereumManager.getAllTransactions();
            int totalTransactionCount = allTransactions.size();
            Log.d("AdminHome", "Total transactions: " + totalTransactionCount);

            requireActivity().runOnUiThread(() -> {
                totalTokensText.setText(totalTokensMinted.toString() + " HKUT");
                totalTransactionsText.setText(String.valueOf(totalTransactionCount));
            });
            Log.d("AdminHome", "> Blockchain data loaded successfully");
            ethereumManager.checkContractState();

        } catch (Exception e) {
            Log.e("AdminHome", "Error loading blockchain data: " + e.getMessage());
            requireActivity().runOnUiThread(() -> {
                User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");
                loadSQLiteData(thisUser);
            });
        }
    }

    private void loadSQLiteData(User thisUser) {
        if (thisUser != null) {
            int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
            int currentBalance = mysqliteopenhelper.getUserBalance(uid);
            totalTokensText.setText(String.valueOf(-currentBalance) + " (SQLite)");
        } else {
            totalTokensText.setText("Balance not available");
        }
    
        int count = mysqliteopenhelper.countTrans();
        totalTransactionsText.setText(String.valueOf(count) + " (SQLite)");
    }
    

    // 设置 Tokens Mined 的折线图
    private void setupTokensMinedChart() {
        try {
            ArrayList<Entry> entries = new ArrayList<>();
            Map<String, Integer> tokenStats = mysqliteopenhelper.getTokenMiningStatsByDateRange(selectedRange);

            // 添加调试日志
            Log.d("AdminHomeFragment", "原始tokenStats: " + tokenStats.toString());
            Log.d("AdminHomeFragment", "当前时间范围: " + selectedRange);

            // 填充缺失的日期
            Map<String, Integer> filledStats = mysqliteopenhelper.fillMissingDates(tokenStats, selectedRange);

            // 添加调试日志
            Log.d("AdminHomeFragment", "填充后的filledStats: " + filledStats.toString());

            if (selectedRange.equals("Monthly")) {
                // Monthly模式：确保按月份顺序处理数据
                Log.d("AdminHomeFragment", "=== Monthly模式数据处理 ===");
                for (int month = 1; month <= 12; month++) {
                    String monthStr = String.format("%02d", month); // 01, 02, ..., 12
                    int value = filledStats.getOrDefault(monthStr, 0);
                    int chartIndex = month - 1; // 索引从0开始，所以month-1
                    entries.add(new Entry(chartIndex, value));
                    Log.d("AdminHomeFragment", String.format("月份: %s, 图表索引: %d, 值: %d", monthStr, chartIndex, value));
                }
                Log.d("AdminHomeFragment", "=== Monthly模式数据处理完成 ===");
            } else {
                // Weekly模式：按原有逻辑处理
                Log.d("AdminHomeFragment", "=== Weekly模式数据处理 ===");
                int index = 0;
                for (Map.Entry<String, Integer> entry : filledStats.entrySet()) {
                    entries.add(new Entry(index, entry.getValue().floatValue()));
                    Log.d("AdminHomeFragment", "Weekly Entry: index=" + index + ", value=" + entry.getValue() + ", key=" + entry.getKey());
                    index++;
                }
                Log.d("AdminHomeFragment", "=== Weekly模式数据处理完成 ===");
            }
            Log.d("AdminHomeFragment", "=== 最终Entries ===");
            for (int i = 0; i < entries.size(); i++) {
                Entry entry = entries.get(i);
                Log.d("AdminHomeFragment", String.format("Entry[%d]: x=%.1f, y=%.1f", i, entry.getX(), entry.getY()));
            }

            if (tokensMinedChart != null) {
                LineDataSet dataSet = new LineDataSet(entries, "Tokens Distributed");
                dataSet.setColor(0xFF1976D2); // 使用硬编码颜色避免崩溃
                dataSet.setLineWidth(2f);
                dataSet.setValueTextColor(0xFF424242); // 使用硬编码颜色避免崩溃
                LineData lineData = new LineData(dataSet);

                tokensMinedChart.setData(lineData);
                configureLineChart(tokensMinedChart);

                // 设置X轴标签
                if (selectedRange.equals("Monthly")) {
                    setupMonthlyXAxis(tokensMinedChart);
                } else {
                    resetXAxis(tokensMinedChart);
                }
            }

        } catch (Exception e) {
            Log.e("AdminHomeFragment", "setupTokensMinedChart崩溃: " + e.getMessage(), e);
        }
    }

    // 设置 Transactions 的柱形图
    private void setupTransactionsChart() {
        try {
            ArrayList<Entry> entries = new ArrayList<>();
            Map<String, Integer> transactionStats = mysqliteopenhelper.getTransactionStatsByDateRange(selectedRange);

            // 填充缺失的日期
            Map<String, Integer> filledStats = mysqliteopenhelper.fillMissingDates(transactionStats, selectedRange);

            if (selectedRange.equals("Monthly")) {
                // Monthly模式：确保按月份顺序处理数据
                for (int month = 1; month <= 12; month++) {
                    String monthStr = String.format("%02d", month); // 01, 02, ..., 12
                    int value = filledStats.getOrDefault(monthStr, 0);
                    int chartIndex = month - 1; // 索引从0开始，所以month-1
                    entries.add(new Entry(chartIndex, value));
                }
            } else {
                // Weekly模式：按原有逻辑处理
                int index = 0;
                for (Map.Entry<String, Integer> entry : filledStats.entrySet()) {
                    entries.add(new Entry(index, entry.getValue().floatValue()));
                    index++;
                }
            }

            if (transactionsChart != null) {
                LineDataSet dataSet = new LineDataSet(entries, "Transactions");
                dataSet.setColor(0xFFE1BEE7); // 使用硬编码颜色避免崩溃
                dataSet.setLineWidth(2f);
                dataSet.setValueTextColor(0xFF424242); // 使用硬编码颜色避免崩溃
                LineData lineData = new LineData(dataSet);

                transactionsChart.setData(lineData);
                configureLineChart(transactionsChart);

                // 设置X轴标签
                if (selectedRange.equals("Monthly")) {
                    setupMonthlyXAxis(transactionsChart);
                } else {
                    resetXAxis(transactionsChart);
                }
            }
        } catch (Exception e) {
            Log.e("AdminHomeFragment", "setupTransactionsChart崩溃: " + e.getMessage(), e);
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

    // 设置月度的X轴标签
    private void setupMonthlyXAxis(com.github.mikephil.charting.charts.Chart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // 设置月份标签 - 确保索引映射正确
        final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                // 关键修复：确保索引正确映射
                // 索引0对应Jan，索引5对应Jun，索引7对应Aug
                if (index >= 0 && index < months.length) {
                    return months[index];
                }
                return "";
            }
        });

        xAxis.setLabelCount(12, true);
        chart.invalidate();
    }

    // 重置X轴为默认设置
    private void resetXAxis(com.github.mikephil.charting.charts.Chart chart) {
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

}
