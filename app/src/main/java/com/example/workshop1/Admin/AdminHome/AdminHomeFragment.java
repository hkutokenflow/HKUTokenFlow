package com.example.workshop1.Admin.AdminHome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class AdminHomeFragment extends Fragment {

    private TextView totalTokensText;
    private TextView totalTransactionsText;

    private LineChart tokensMinedChart;
    private BarChart transactionsChart;
    private Spinner chartTimeRangeSpinner;
    private String selectedRange = "Weekly";
    private Mysqliteopenhelper mysqliteopenhelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_home, container, false);

        mysqliteopenhelper = new Mysqliteopenhelper(getContext());
        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");

        totalTokensText = root.findViewById(R.id.total_tokens_value);
        if (thisUser != null) {
            int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
            int currentBalance = mysqliteopenhelper.getUserBalance(uid);
            totalTokensText.setText(String.valueOf(-currentBalance));
        } else {
            totalTokensText.setText("Balance not available");
        }

        totalTransactionsText = root.findViewById(R.id.total_transactions_value);
        int count = mysqliteopenhelper.countTrans();
        totalTransactionsText.setText(String.valueOf(count));


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
        if (thisUser != null) {
            int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
            int currentBalance = mysqliteopenhelper.getUserBalance(uid);
            totalTokensText.setText(String.valueOf(-currentBalance));
        }
        int count = mysqliteopenhelper.countTrans();
        totalTransactionsText.setText(String.valueOf(count));
    }


    // 设置 Tokens Mined 的折线图
    private void setupTokensMinedChart() {
        ArrayList<Entry> entries = new ArrayList<>();
        if (selectedRange.equals("Weekly")) {
            for (int i = 1; i <= 7; i++) {
                entries.add(new Entry(i, (float) (100 + Math.random() * 50)));
            }
        } else {
            for (int i = 1; i <= 12; i++) {
                entries.add(new Entry(i, (float) (300 + Math.random() * 100)));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Tokens Mined");
        dataSet.setColor(getResources().getColor(R.color.teal_700));
        dataSet.setLineWidth(2f);
        dataSet.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark));
        LineData lineData = new LineData(dataSet);

        tokensMinedChart.setData(lineData);
        configureLineChart(tokensMinedChart);
    }

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
        dataSet.setColor(getResources().getColor(R.color.purple_100));
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        transactionsChart.setData(barData);
        configureBarChart(transactionsChart);
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
}
