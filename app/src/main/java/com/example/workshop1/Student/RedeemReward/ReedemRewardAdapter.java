package com.example.workshop1.Student.RedeemReward;

import android.app.Dialog;
import android.app.ProgressDialog;
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

import com.example.workshop1.Ethereum.EthereumManager;
import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.StudentReward;
import com.example.workshop1.SQLite.Transaction;
import com.example.workshop1.SQLite.User;

import java.math.BigInteger;
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
    private EthereumManager ethereumManager;
    private ProgressDialog progressDialog;

    public ReedemRewardAdapter(Context context, List<RewardItem> rewards, User user) {
        this.context = context;
        this.rewardList = rewards;
        this.thisUser = user;
        this.mysqliteopenhelper = new Mysqliteopenhelper(context);
        this.ethereumManager = new EthereumManager(context);
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

            showRedeemProgress(reward.title, reward.tokens);

            new Thread(() -> {
                try {
                    boolean initialized = ethereumManager.initializeSecurely();
                    if (!initialized) {
                        Log.e("RedeemRewards", "Failed to initialize EthereumManager");
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            dismissRedeemProgress();
                            Toast.makeText(context, "Blockchain unavailable", Toast.LENGTH_LONG).show();
                        });
                        return;
                    }

                    // Get user IDs and wallet addresses
                    int studentId = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
                    String studentWallet = thisUser.getWallet();
                    String vendorWallet = mysqliteopenhelper.getUserWallet(reward.uid);

                    if (studentWallet == null || vendorWallet == null) {
                        Log.e("RedeemRewards", "Missing wallet addresses - Student: " + studentWallet + ", Vendor: " + vendorWallet);
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            dismissRedeemProgress();
                            Toast.makeText(context, "Wallet addresses not found", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        });
                        return;
                    }

                    // Check if balance is enough
                    BigInteger currentBalance = ethereumManager.getBalance(studentWallet);
                    BigInteger requiredAmount = BigInteger.valueOf(reward.tokens);
                    Log.d("RedeemRewards", "Blockchain balance: " + currentBalance);
                    Log.d("RedeemRewards", "Required amount: " + requiredAmount);

                    if (currentBalance.compareTo(requiredAmount) >= 0) {

                        Log.d("RedeemRewards", "=== STARTING BLOCKCHAIN TRANSFER ===");
                        Log.d("RedeemRewards", "From: " + studentWallet);
                        Log.d("RedeemRewards", "To: " + vendorWallet);
                        Log.d("RedeemRewards", "Amount: " + requiredAmount + " tokens");
                        BigInteger studentBalanceBefore = ethereumManager.getBalance(studentWallet);
                        BigInteger vendorBalanceBefore = ethereumManager.getBalance(vendorWallet);
                        Log.d("RedeemRewards", "Student balance before: " + studentBalanceBefore);
                        Log.d("RedeemRewards", "Vendor balance before: " + vendorBalanceBefore);

                        try {
                            ethereumManager.transferTokens(studentWallet, vendorWallet, requiredAmount);

                            BigInteger studentBalanceAfter = ethereumManager.getBalance(studentWallet);
                            BigInteger vendorBalanceAfter = ethereumManager.getBalance(vendorWallet);
                            Log.d("RedeemRewards", "Student balance after: " + studentBalanceAfter);
                            Log.d("RedeemRewards", "Vendor balance after: " + vendorBalanceAfter);
                            // Verify transfer
                            BigInteger expectedStudentBalance = studentBalanceBefore.subtract(requiredAmount);
                            BigInteger expectedVendorBalance = vendorBalanceBefore.add(requiredAmount);
                            Log.d("RedeemRewards", "Expected student balance: " + expectedStudentBalance);
                            Log.d("RedeemRewards", "Expected vendor balance: " + expectedVendorBalance);
                            boolean transferSuccessful = studentBalanceAfter.equals(expectedStudentBalance) && vendorBalanceAfter.equals(expectedVendorBalance);
                            if (transferSuccessful) {
                                Log.d("RedeemRewards", "TRANSFER VERIFICATION SUCCESSFUL");
                            } else {
                                Log.e("RedeemRewards", "TRANSFER VERIFICATION FAILED");
                                Log.e("RedeemRewards", "Student balance mismatch: expected " + expectedStudentBalance + ", got " + studentBalanceAfter);
                                Log.e("RedeemRewards", "Vendor balance mismatch: expected " + expectedVendorBalance + ", got " + vendorBalanceAfter);
                            }

                            // update SQLite
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                updateSQL(reward, dialog);
                                dismissRedeemProgress();
                                Toast.makeText(context, "Reward Redeemed!", Toast.LENGTH_SHORT).show();
                            });
                            
                        } catch (Exception e) {
                            Log.e("RedeemRewards", "Blockchain transfer failed: " + e.getMessage());
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                dismissRedeemProgress();
                                Toast.makeText(context, "Blockchain transfer failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            });
                        }
                    } else {
                        Log.e("RedeemRewards", "Insufficient blockchain balance");
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            dismissRedeemProgress();
                            Toast.makeText(context, "Insufficient blockchain balance", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        });
                    }
                } catch (Exception e) {
                    Log.e("RedeemRewards", "Error in blockchain redemption: " + e.getMessage());
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        dismissRedeemProgress();
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    });
                }
            }).start();

            /* 
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
            } */
        });
    
        dialog.show();
    }

    private void showRedeemProgress(String rewardName, int tokens) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Processing Transaction...");
        progressDialog.setMessage("Transferring " + tokens + " HKUTokens to vendor for '" + rewardName + "'.\nThis may take a moment.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissRedeemProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void updateSQL(RewardItem reward, Dialog dialog) {
        int sid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
        int currentBalance = mysqliteopenhelper.getUserBalance(sid);

        // Add record into StudentRewards
        int rewardId = mysqliteopenhelper.getRewardId(reward.title, reward.description, reward.tokens, reward.uid);
        Log.d("RedeemRewards", "rewardId: " + rewardId);
        Log.d("RedeemRewards", "reward uid: " + reward.uid);
        StudentReward sr = new StudentReward(sid, rewardId);
        mysqliteopenhelper.addStudentReward(sr);

        // Add transaction + update user balances
        Calendar calendar = Calendar.getInstance();  
        TimeZone hktTimeZone = TimeZone.getTimeZone("Asia/Hong_Kong");  
        calendar.setTimeZone(hktTimeZone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String formattedDateTime = sdf.format(calendar.getTime());
        Transaction trans = new Transaction(formattedDateTime, sid, reward.uid, reward.tokens, rewardId, "r");
        mysqliteopenhelper.addTransaction(trans);
        thisUser.setBalance(currentBalance - reward.tokens);

        dialog.dismiss();
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
