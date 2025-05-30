package com.example.workshop1.Student.RedeemReward;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.User;
import com.example.workshop1.Vendor.Voucher.VoucherItem;

import java.util.ArrayList;
import java.util.List;

// RedeemRewardsFragment.java
public class RedeemRewardFragment extends Fragment {

    RecyclerView recyclerView;
    ReedemRewardAdapter adapter;
    List<RewardItem> rewards;
    private Mysqliteopenhelper mysqliteopenhelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_redeem_reward, container, false);
        recyclerView = view.findViewById(R.id.rewards_recycler_view);
        rewards = new ArrayList<>();

        mysqliteopenhelper = new Mysqliteopenhelper(requireContext());
        Cursor cursor = mysqliteopenhelper.getRewardsAll();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                int value = cursor.getInt(3);
                int uid = cursor.getInt(4);
                Log.d("RedeemRewardsFrag", "Reward uid " + uid);
                rewards.add(new RewardItem(name, description, value, uid));
            }
        }

        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");

        // 每行两个卡片
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ReedemRewardAdapter(getContext(), rewards, thisUser);
        recyclerView.setAdapter(adapter);

        return view;



        /* //第一个是用来测试长度较长的description的,在卡片里面会截取出来前xx位字母
        rewards.add(new RewardItem("Gift Voucher", "UprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprintUprint", 4000));
        rewards.add(new RewardItem("$10 Fare Discount", "Collect via App", 3000));
        rewards.add(new RewardItem("Free Domestic Ride", "Valid for one ride only", 5000));
        rewards.add(new RewardItem("Shopping Coupon", "Redeemable in Malls", 3500));
        rewards.add(new RewardItem("Shopping Coupon", "Redeemable in Malls", 3500));*/


    }
}
