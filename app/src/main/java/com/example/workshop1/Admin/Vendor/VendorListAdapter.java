package com.example.workshop1.Admin.Vendor;


import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
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

public class VendorListAdapter extends ArrayAdapter<VendorItem> {

    private Context context;
    private List<VendorItem> vendorList;
    private Mysqliteopenhelper mysqliteopenhelper;

    public VendorListAdapter(@NonNull Context context, List<VendorItem> list) {
        super(context, 0, list);
        this.context = context;
        this.vendorList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.vendor_list_item, parent, false);
        }

        VendorItem currentVendor = vendorList.get(position);

        TextView nameView = convertView.findViewById(R.id.tv_vendor_name);
        ImageButton viewButton = convertView.findViewById(R.id.btn_view);
        ImageButton editButton = convertView.findViewById(R.id.btn_edit);
        ImageButton deleteButton = convertView.findViewById(R.id.btn_delete);
        mysqliteopenhelper = new Mysqliteopenhelper(context);

        nameView.setText(currentVendor.name);

        //-------------------------------VIEW-------------------------------------
        viewButton.setOnClickListener(v -> {
            String username = currentVendor.getUsername();
            String password = currentVendor.getPassword();
            String name = currentVendor.getName();

            // Create and show a dialog to display the username and password
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(name);
            builder.setMessage("Username: " + username + "\nPassword: " + password);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        });


        //-------------------------------EDIT-------------------------------------
        editButton.setOnClickListener(v -> {
            showEditDialog(currentVendor, position);
        });

        //-------------------------------DELETE------------------------------------
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Vendor")
                    .setMessage("Are you sure you want to delete \"" + currentVendor.name + "\"?")

                    .setPositiveButton("Delete", (dialog, which) -> {
                        mysqliteopenhelper.deleteVendor(currentVendor.getUsername());
                        vendorList.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Vendor deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return convertView;
    }

    private void showEditDialog(VendorItem vendor, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_vendor, null);
        EditText etName = dialogView.findViewById(R.id.et_vendor_name);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
        mysqliteopenhelper = new Mysqliteopenhelper(context);

        // 设置原有值
        etName.setText(vendor.name);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(context, "Please fill in the name", Toast.LENGTH_SHORT).show();
                return;
            }

            // 更新 vendor 数据
            String username = vendor.getUsername();
            mysqliteopenhelper.editVendorName(newName, username);

            vendor.name = newName;

            notifyDataSetChanged();  // 通知刷新
            dialog.dismiss();
        });

        dialog.show();
    }
}
