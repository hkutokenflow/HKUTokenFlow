package com.example.workshop1.Admin.RecentTransaction;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.Student.StudentHome.StudentHomeFragment;

import java.util.ArrayList;
import java.util.List;

public class RecentTransactionsFragment extends Fragment {

    private TableLayout transactionsTable;
    private EditText searchEditText;
    private Mysqliteopenhelper mysqliteopenhelper;

    private List<Transaction> allTransactions = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recent_transactions, container, false);

        mysqliteopenhelper = new Mysqliteopenhelper(getContext());

        // transaction table
        transactionsTable = root.findViewById(R.id.recent_transactions_table);
        searchEditText = root.findViewById(R.id.transaction_search);

        Cursor cursor = mysqliteopenhelper.getAllTrans();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String datetime = cursor.getString(1);
                int src = cursor.getInt(2);
                String srcType = mysqliteopenhelper.getUserType(src);
                int dest = cursor.getInt(3);
                String destType = mysqliteopenhelper.getUserType(dest);
                int amt = cursor.getInt(4);
                allTransactions.add(new Transaction(datetime, String.valueOf(src)+srcType, String.valueOf(dest)+destType, String.valueOf(amt)));
            }
        }

        // setupDummyData(); // 你也可以从数据库中读取
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
