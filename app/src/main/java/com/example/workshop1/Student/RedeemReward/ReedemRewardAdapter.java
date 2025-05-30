package com.example.workshop1.Student.RedeemReward;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.StudentReward;
import com.example.workshop1.SQLite.Transaction;
import com.example.workshop1.SQLite.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

// RewardAdapter.java
public class ReedemRewardAdapter extends RecyclerView.Adapter<ReedemRewardAdapter.ViewHolder> {

    private List<RewardItem> rewardList;
    private Context context;
    private User thisUser;
    private Mysqliteopenhelper mysqliteopenhelper;

    public ReedemRewardAdapter(Context context, List<RewardItem> rewards, User user) {
        this.context = context;
        this.rewardList = rewards;
        this.thisUser = user;
    }

    @NonNull
    @Override
    public ReedemRewardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reward, parent, false);
        return new ReedemRewardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RewardItem reward = rewardList.get(position);
        holder.title.setText(reward.title);
        holder.tokens.setText(reward.tokens + " Tokens");

        // ----------------截取Description的前40个字-----------------------------------
        String fullDesc = reward.description != null ? reward.description : "";
        String preview = fullDesc.length() > 40 ? fullDesc.substring(0, 40) + "..." : fullDesc;
        holder.shortDesc.setText(preview);

        holder.itemView.setOnClickListener(v -> showRewardDialog(context, reward));
    }


    private void showRewardDialog(Context context, RewardItem reward) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_reward_description);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView desc = dialog.findViewById(R.id.dialog_description);
        TextView tokens = dialog.findViewById(R.id.dialog_tokens);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        title.setText(reward.title);
        desc.setText(reward.description);
        tokens.setText(reward.tokens + " Tokens");

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            mysqliteopenhelper = new Mysqliteopenhelper(context);

            // Get the user's current balance from the database
            int sid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
            int currentBalance = mysqliteopenhelper.getUserBalance(sid);

            // Check if balance is enough
            Log.d("RedeemRewards", "User balance " + currentBalance);
            Log.d("RedeemRewards", "Reward cost " + reward.tokens);
            if (currentBalance >= reward.tokens) {

                // Add record into StudentRewards
                int rewardId = mysqliteopenhelper.getRewardId(reward.title, reward.description, reward.tokens, reward.uid);
                Log.d("Redeem Voucher", "rewardId: " + rewardId);
                Log.d("Redeem Voucher", "reward uid: " + reward.uid);
                StudentReward sr = new StudentReward(sid, rewardId);
                mysqliteopenhelper.addStudentReward(sr);

                // Add transaction + update user balances
                Calendar calendar = Calendar.getInstance();  // Create a Calendar instance
                TimeZone hktTimeZone = TimeZone.getTimeZone("Asia/Hong_Kong");  // Set the timezone to HKT
                calendar.setTimeZone(hktTimeZone);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                String formattedDateTime = sdf.format(calendar.getTime());
                Transaction trans = new Transaction(formattedDateTime, sid,  reward.uid, reward.tokens, rewardId, "r");
                mysqliteopenhelper.addTransaction(trans);
                thisUser.setBalance(currentBalance - reward.tokens);

                Toast.makeText(context, "Reward Redeemed!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Insufficient balance", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, tokens, shortDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.reward_title);
            tokens = itemView.findViewById(R.id.reward_tokens);
            shortDesc = itemView.findViewById(R.id.reward_short_desc);
        }
    }
}
