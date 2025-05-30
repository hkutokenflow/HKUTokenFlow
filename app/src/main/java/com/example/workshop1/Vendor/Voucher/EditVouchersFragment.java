package com.example.workshop1.Vendor.Voucher;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.workshop1.Admin.Vendor.VendorItem;
import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.Reward;
import com.example.workshop1.SQLite.User;
import com.example.workshop1.Vendor.Voucher.VoucherItem;
import com.example.workshop1.Vendor.Voucher.VoucherListAdapter;

import java.util.ArrayList;
import java.util.List;


public class EditVouchersFragment extends Fragment {

    private ListView voucherListView;
    private Button addButton;
    private List<VoucherItem> voucherList;
    private VoucherListAdapter adapter;
    private Mysqliteopenhelper mysqliteopenhelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_vouchers, container, false);

        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");

        mysqliteopenhelper = new Mysqliteopenhelper(requireContext());

        int vendorId = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
        Log.d("editReward", "vendor id" + vendorId);

        voucherListView = view.findViewById(R.id.voucher_list_view);
        addButton = view.findViewById(R.id.btn_add);
        voucherList = new ArrayList<>();

        //-------------------------ADD----------------------------------
        Cursor cursor = mysqliteopenhelper.getRewardsVendor(vendorId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                int value = cursor.getInt(3);
                voucherList.add(new VoucherItem(name, description, value));
            }
        }

        //-------------------EDIT和DELETE都在这边----------------------
        adapter = new VoucherListAdapter(getContext(), voucherList);
        voucherListView.setAdapter(adapter);

        addButton.setOnClickListener(v -> showAddVoucherDialog());

        return view;
    }


    private void showAddVoucherDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_voucher, null);
        EditText nameInput = dialogView.findViewById(R.id.et_voucher_name);
        EditText tokenInput = dialogView.findViewById(R.id.et_token_count);
        EditText descInput = dialogView.findViewById(R.id.et_voucher_description);
        Button confirmButton = dialogView.findViewById(R.id.btn_confirm);
        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        confirmButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String tokenStr = tokenInput.getText().toString().trim();
            String description = descInput.getText().toString().trim();

            if (name.isEmpty() || tokenStr.isEmpty() || description.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int tokens = Integer.parseInt(tokenStr);

                User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");
                Log.d("CreateReward", thisUser.getUsername());
                Log.d("CreateReward", thisUser.getPassword());

                int vendorId = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
                Log.d("CreateReward", "vendor id " + vendorId);

                Reward reward = new Reward(name, description, tokens, vendorId);
                long res = mysqliteopenhelper.addReward(reward);
                if (res != -1) {
                    Toast.makeText(requireContext(), "Reward added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to add reward.", Toast.LENGTH_SHORT).show();
                }

                voucherList.add(new VoucherItem(name, description, tokens));
                adapter.notifyDataSetChanged();
                dialog.dismiss();

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Token amount must be a number", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}


