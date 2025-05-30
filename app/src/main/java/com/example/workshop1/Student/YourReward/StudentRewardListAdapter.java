package com.example.workshop1.Student.YourReward;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
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
import com.example.workshop1.SQLite.User;

import java.util.List;

public class StudentRewardListAdapter extends ArrayAdapter<StudentRewardItem> {

    private Context context;
    private List<StudentRewardItem> studentRewardList;
    private Mysqliteopenhelper mysqliteopenhelper;
    private User thisUser;

    public StudentRewardListAdapter(@NonNull Context context, List<StudentRewardItem> list, User user) {
        super(context, 0, list);
        this.context = context;
        this.studentRewardList = list;
        this.thisUser = user;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.student_reward_list_item, parent, false);
        }

        mysqliteopenhelper = new Mysqliteopenhelper(context);

        StudentRewardItem currentStudentReward = studentRewardList.get(position);

        TextView nameView = convertView.findViewById(R.id.tv_voucher_name);
        Button useButton = convertView.findViewById(R.id.btn_use);

        nameView.setText(currentStudentReward.voucher);


        //-------------------------------DELETE------------------------------------
        useButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Use this reward?")
                    .setMessage(Html.fromHtml("<b>" + currentStudentReward.voucher + ":</b> " + currentStudentReward.description + "<br><br><i>This action cannot be undone.</i>", Html.FROM_HTML_MODE_LEGACY)) // Use <br> for line breaks
                    .setPositiveButton("Use", (dialog, which) -> {
                        int rid = mysqliteopenhelper.getRewardId(currentStudentReward.voucher, currentStudentReward.description, currentStudentReward.value, currentStudentReward.uid);
                        int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
                        mysqliteopenhelper.deleteStudentReward(uid, rid);

                        studentRewardList.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Reward used", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        return convertView;
    }


}
