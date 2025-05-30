package com.example.workshop1.Vendor.VendorHome;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.User;
import com.example.workshop1.Student.StudentHome.StudentHomeFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class VendorHomeFragment extends Fragment {

    private TextView vendorWalletText;


    private BarChart vendorTransactionsChart;
    private Spinner chartTimeRangeSpinner;
    private String selectedRange = "Weekly";
    private TableLayout transactionsTable;
    private EditText searchEditText;
    private List<Transaction> allTransactions = new ArrayList<>();
    private Mysqliteopenhelper mysqliteopenhelper;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_vendor_home, container, false);

        mysqliteopenhelper = new Mysqliteopenhelper(getContext());
        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");

        // wallet balance
        vendorWalletText = root.findViewById(R.id.vendor_wallet_balance);
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

    //-----------------------------------barchart--------------------------------
    // 设置 Transactions 的柱形图
    private void setupTransactionsChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        if (selectedRange.equals("Weekly")) {
            for (int i = 1; i <= 7; i++) {
                entries.add(new BarEntry(i, (float) (50 + Math.random() * 30)));
            }
        } else {
            for (int i = 1; i <= 12; i++) {
                entries.add(new BarEntry(i, (float) (200 + Math.random() * 100)));
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Transactions");
        dataSet.setColor(getResources().getColor(R.color.blue_200));
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        vendorTransactionsChart.setData(barData);
        configureBarChart(vendorTransactionsChart);
    }

    @Override
    public void onResume() {
        super.onResume();
        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");
        if (thisUser != null) {
            int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
            int currentBalance = mysqliteopenhelper.getUserBalance(uid);
            vendorWalletText.setText(String.valueOf(currentBalance));
        }
    }


    // 配置柱形图
    private void configureBarChart(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setFitBars(true);
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






