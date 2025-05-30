package com.example.workshop1.Admin.Vendor;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.User;
import com.example.workshop1.SQLite.VendorApproval;

import java.util.List;

public class VendorApprovalAdapter extends ArrayAdapter<VendorApprovalItem> {
    private Context context;
    private List<VendorApprovalItem> approvalList;
    private Mysqliteopenhelper mysqliteopenhelper;

    public VendorApprovalAdapter(@NonNull Context context, List<VendorApprovalItem> list) {
        super(context, 0, list);
        this.context = context;
        this.approvalList = list;
        this.mysqliteopenhelper = new Mysqliteopenhelper(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.vendor_list_item, parent, false);
        }

        VendorApprovalItem currentItem = approvalList.get(position);

        TextView nameView = convertView.findViewById(R.id.tv_vendor_name);
        TextView statusView = convertView.findViewById(R.id.tv_approval_status);
        ImageButton viewButton = convertView.findViewById(R.id.btn_view);
        ImageButton actionButton = convertView.findViewById(R.id.btn_action);

        nameView.setText(currentItem.getName());
        statusView.setText(currentItem.getStatusText());
        statusView.getBackground().setTint(currentItem.getStatusColor());

        // 只有未处理的才显示操作按钮
        actionButton.setVisibility(currentItem.getApproved() == 0 ? View.VISIBLE : View.GONE);

        // 查看详情
        viewButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(currentItem.getName());
            builder.setMessage("Username: " + currentItem.getUsername());
            
            if (currentItem.getApproved() == 0) {
                builder.setPositiveButton("Approve", (dialog, which) -> {
                    // 1. 更新 VendorApproval 表中的状态
                    mysqliteopenhelper.updateVendorApprovalStatus(currentItem.getUsername(), 1);
                    
                    // 2. 添加到 Users 表，使用原始的密码
                    User user = new User(currentItem.getUsername(), 
                                      currentItem.getPassword(),
                                      currentItem.getName(), 
                                      "vendor");
                    long res = mysqliteopenhelper.addUser(user);
                    
                    if (res != -1) {
                        // 3. 更新列表项状态
                        currentItem.setApproved(1);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Vendor approved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to approve vendor", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Refuse", (dialog, which) -> {
                    // 更新 VendorApproval 表中的状态为拒绝
                    mysqliteopenhelper.updateVendorApprovalStatus(currentItem.getUsername(), 2);
                    currentItem.setApproved(2);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Vendor refused", Toast.LENGTH_SHORT).show();
                });
                builder.setNeutralButton("Cancel", null);
            } else {
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            }
            
            AlertDialog dialog = builder.create();
            dialog.show();
            
            // Set the refuse button color to red if it exists
            if (currentItem.getApproved() == 0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFFF0000);
            }
        });

        // Remove the action button click listener since we moved the functionality to the view dialog
        actionButton.setVisibility(View.GONE);

        return convertView;
    }
} 