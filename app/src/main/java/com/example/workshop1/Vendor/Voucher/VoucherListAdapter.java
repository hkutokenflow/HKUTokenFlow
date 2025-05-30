package com.example.workshop1.Vendor.Voucher;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;

import java.util.List;

public class VoucherListAdapter extends ArrayAdapter<VoucherItem> {

    private Context context;
    private List<VoucherItem> voucherList;
    private Mysqliteopenhelper mysqliteopenhelper;

    public VoucherListAdapter(@NonNull Context context, List<VoucherItem> list) {
        super(context, 0, list);
        this.context = context;
        this.voucherList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.voucher_list_item, parent, false);
        }

        VoucherItem currentVoucher = voucherList.get(position);

        TextView nameView = convertView.findViewById(R.id.tv_voucher_name);
        TextView tokenView = convertView.findViewById(R.id.tv_token_count);
        ImageButton viewButton = convertView.findViewById(R.id.btn_view);
        ImageButton editButton = convertView.findViewById(R.id.btn_edit);
        ImageButton deleteButton = convertView.findViewById(R.id.btn_delete);

        mysqliteopenhelper = new Mysqliteopenhelper(context);

        nameView.setText(currentVoucher.name);
        tokenView.setText(currentVoucher.tokens + " tokens");

        //-------------------------------VIEW-------------------------------------
        viewButton.setOnClickListener(v -> {
            String name = currentVoucher.getName();
            String description = currentVoucher.getDescription();
            int reward = currentVoucher.getTokens();

            // Create and show a dialog to display the username and password
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(name + " (" + reward + " tokens)");
            builder.setMessage(description);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        });


        //-------------------------------EDIT-------------------------------------
        editButton.setOnClickListener(v -> {
            showEditDialog(currentVoucher, position);
        });

        //-------------------------------DELETE------------------------------------
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Voucher")
                    .setMessage("Are you sure you want to delete \"" + currentVoucher.name + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        mysqliteopenhelper.deleteReward(currentVoucher.getName(), currentVoucher.getDescription());
                        voucherList.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Voucher deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        return convertView;
    }


    private void showEditDialog(VoucherItem voucher, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_voucher, null);
        EditText etName = dialogView.findViewById(R.id.et_voucher_name);
        EditText etDesc = dialogView.findViewById(R.id.et_voucher_description);
        EditText etTokens = dialogView.findViewById(R.id.et_token_count);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
        mysqliteopenhelper = new Mysqliteopenhelper(context);

        // 设置原有值
        etName.setText(voucher.name);
        etDesc.setText(voucher.description);
        etTokens.setText(String.valueOf(voucher.tokens));

        String orgName = voucher.name;
        String orgDesc = voucher.description;

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newDesc = etName.getText().toString().trim();
            String tokenStr = etTokens.getText().toString().trim();

            if (newName.isEmpty() || tokenStr.isEmpty() || newDesc.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int newTokens = Integer.parseInt(tokenStr);

            // 更新 event 数据
            mysqliteopenhelper.editReward(newName, newDesc, newTokens, orgName, orgDesc);

            voucher.name = newName;
            voucher.description = newDesc;
            voucher.tokens = newTokens;

            notifyDataSetChanged();  // 通知刷新
            dialog.dismiss();
        });

        dialog.show();
    }



}
