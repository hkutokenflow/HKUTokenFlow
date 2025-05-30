package com.example.workshop1.Admin.Vendor;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;

import java.util.ArrayList;
import java.util.List;

public class ManageVendorFragment extends Fragment {

    private ListView vendorListView;
    private List<VendorApprovalItem> approvalList;
    private VendorApprovalAdapter adapter;
    private Mysqliteopenhelper mysqliteopenhelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_vendor, container, false);

        vendorListView = view.findViewById(R.id.vendor_list_view);
        approvalList = new ArrayList<>();

        mysqliteopenhelper = new Mysqliteopenhelper(requireContext());
        Cursor cursor = mysqliteopenhelper.getVendorApprovals();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String username = cursor.getString(1);
                String password = cursor.getString(2);
                String name = cursor.getString(3);
                int approved = cursor.getInt(4);
                approvalList.add(new VendorApprovalItem(username, password, name, approved));
            }
        }

        adapter = new VendorApprovalAdapter(getContext(), approvalList);
        vendorListView.setAdapter(adapter);

        return view;
    }
}
