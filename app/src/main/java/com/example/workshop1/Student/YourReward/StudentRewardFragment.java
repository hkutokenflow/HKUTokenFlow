package com.example.workshop1.Student.YourReward;

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

import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.User;

import java.util.ArrayList;
import java.util.List;


public class StudentRewardFragment extends Fragment {

    private ListView rewardListView;
    private List<StudentRewardItem> rewardList;
    private StudentRewardListAdapter adapter;
    private Mysqliteopenhelper mysqliteopenhelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_reward, container, false);
        rewardListView = view.findViewById(R.id.student_reward_list_view);
        rewardList = new ArrayList<>();

        mysqliteopenhelper = new Mysqliteopenhelper(requireContext());
        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");
        Log.d("YourRewards", "User:" + thisUser.getUsername());
        int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
        Log.d("YourRewards", "uid:" + uid);

        Cursor cursor = mysqliteopenhelper.getStudentRewards(uid);
        Log.d("YourRewards", "student rewards count:" + cursor.getCount());
        if (cursor.getCount() != 0) {
            while(cursor.moveToNext()) {
                int rid = cursor.getInt(2);
                Log.d("YourRewards", "rid (_id):" + rid);
                Cursor res = mysqliteopenhelper.getRewardFromId(rid);
                if (res.getCount() != 0) {
                    res.moveToNext();
                    String r_name = res.getString(1);
                    String r_desc = res.getString(2);
                    int r_value = res.getInt(3);
                    int r_uid = res.getInt(4);
                    rewardList.add(new StudentRewardItem(r_name, r_desc, r_value, r_uid));
                }
            }
        }

        //-------------------DELETE(use)在这边----------------------
        adapter = new StudentRewardListAdapter(getContext(), rewardList, thisUser);
        rewardListView.setAdapter(adapter);


        return view;
    }



}


