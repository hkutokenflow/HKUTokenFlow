package com.example.workshop1.Admin.Vendor;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
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
import com.example.workshop1.Utils.PasswordEncryption;

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

        // Create a new drawable with the correct color
        GradientDrawable background = (GradientDrawable) context.getResources().getDrawable(R.drawable.status_tag_background).mutate();
        background.setColor(currentItem.getStatusColor());
        statusView.setBackground(background);

        // 根据审批状态显示或隐藏操作按钮
        if (currentItem.getApproved() == 0) {  // 待审批
            actionButton.setVisibility(View.VISIBLE);
            actionButton.setEnabled(true);
        } else {  // 已审批或拒绝
            actionButton.setVisibility(View.GONE);
        }

        // 搜索按钮始终可见
        viewButton.setVisibility(View.VISIBLE);

        //-------------------------------VIEW-------------------------------------
        viewButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(currentItem.getName());
            builder.setMessage("Username: " + currentItem.getUsername() +
                    "\nApproval Status: " + currentItem.getStatusText());
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        //-------------------------------ACTION-------------------------------------
        actionButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Vendor Approval");
            builder.setMessage("Do you want to approve or refuse " + currentItem.getName() + "?");

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
                mysqliteopenhelper.updateVendorApprovalStatus(currentItem.getUsername(), -1);
                currentItem.setApproved(-1);
                notifyDataSetChanged();
                Toast.makeText(context, "Vendor refused", Toast.LENGTH_SHORT).show();
            });

            builder.setNeutralButton("Cancel", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            // 设置按钮颜色
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFF44336); // 红色
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xFF9E9E9E);  // 灰色
        });

        return convertView;
    }
}