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
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.workshop1.Admin.RecentTransaction.RecentTransactionsFragment;
import com.example.workshop1.Admin.Vendor.VendorItem;
import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class StudentHomeFragment extends Fragment {

    private TextView studentWalletText;


    private TableLayout transactionsTable;
    private EditText searchEditText;
    private List<Transaction> allTransactions = new ArrayList<>();
    private Mysqliteopenhelper mysqliteopenhelper;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_student_home, container, false);

        mysqliteopenhelper = new Mysqliteopenhelper(getContext());

        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");

        // wallet balance
        studentWalletText = root.findViewById(R.id.student_wallet_balance);
        if (thisUser != null) {
            int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
            int currentBalance = mysqliteopenhelper.getUserBalance(uid);
            studentWalletText.setText(String.valueOf(currentBalance));
            // studentWalletText.setText(String.valueOf(thisUser.getBalance()));
        } else {
            studentWalletText.setText("Balance not available");
        }

        //transaction table
        transactionsTable = root.findViewById(R.id.recent_transactions_table);
        searchEditText = root.findViewById(R.id.transaction_search);

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

        // setupDummyData(); // SQLite
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

    @Override
    public void onResume() {
        super.onResume();
        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");
        if (thisUser != null) {
            int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
            int currentBalance = mysqliteopenhelper.getUserBalance(uid);
            studentWalletText.setText(String.valueOf(currentBalance));
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
}






