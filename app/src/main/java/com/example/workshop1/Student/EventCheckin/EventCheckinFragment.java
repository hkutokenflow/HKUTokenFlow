package com.example.workshop1.Student.EventCheckin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.example.workshop1.Ethereum.EthereumManager;
import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.Transaction;
import com.example.workshop1.SQLite.User;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EventCheckinFragment extends Fragment {

    private ListView eventListView;
    private List<EventItem> eventList;
    private StudentEventListAdapter adapter;
    private Button scanBtn;
    private String checkInId;  // 用于存储扫描到的eventid
    private Mysqliteopenhelper mysqliteopenhelper;
    private EthereumManager ethereumManager;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_checkin, container, false);

        eventListView = view.findViewById(R.id.student_event_list_view);
        scanBtn = view.findViewById(R.id.btn_scan_qr);
        eventList = new ArrayList<>();

        ethereumManager = new EthereumManager(getContext());

        //------------------------Event List----------------------------------
        mysqliteopenhelper = new Mysqliteopenhelper(requireContext());
        Cursor cursor = mysqliteopenhelper.getEvents();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                int tokens = cursor.getInt(2);
                eventList.add(new EventItem(name, tokens));
            }
        }

        //-----------------------set adapter------------------
        adapter = new StudentEventListAdapter(getContext(), eventList);
        eventListView.setAdapter(adapter);


        //----------------------scan QR code-----------------
        scanBtn.setOnClickListener(v -> startQRScanner());

        return view;
    }

    private void startQRScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan event QR code to check-in");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true); // 保持竖屏
        integrator.setCaptureActivity(CustomScannerActivity.class); // 设置自定义扫码Activity
        integrator.initiateScan();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mysqliteopenhelper = new Mysqliteopenhelper(requireContext());
        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // 获取二维码中的内容（eventid）
                checkInId = result.getContents();  // 将二维码的内容存储在eventId变量中

                // check if check-in id is valid (exists in Events)
                if (!mysqliteopenhelper.checkValidEvent(Integer.parseInt(checkInId))) {
                    Toast.makeText(getContext(), "Check-in unsuccessful\nInvalid event ID", Toast.LENGTH_LONG).show();
                } else {
                    // check if check-in is repeated
                    int uid = mysqliteopenhelper.getUserId(thisUser.getUsername(), thisUser.getPassword());
                    // Toast.makeText(getActivity(), "UID: " + uid + " - Logged in as: " + thisUser.getUsername(), Toast.LENGTH_SHORT).show();
                    int checkInIdNum = Integer.parseInt(checkInId);
                    if (!mysqliteopenhelper.checkRepeatedCheckIn(checkInIdNum, uid)) {
                        Toast.makeText(getContext(), "Check-in unsuccessful\nAlready checked in to this event", Toast.LENGTH_LONG).show();
                    } else {
                        // get current datetime
                        Calendar calendar = Calendar.getInstance();  // Create a Calendar instance
                        TimeZone hktTimeZone = TimeZone.getTimeZone("Asia/Hong_Kong");  // Set the timezone to HKT
                        calendar.setTimeZone(hktTimeZone);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        String formattedDateTime = sdf.format(calendar.getTime());

                        // get event details
                        int reward = mysqliteopenhelper.getEventReward(checkInIdNum);
                        String eName = mysqliteopenhelper.getEventName(checkInIdNum);

                        // Get student's wallet address
                        String studentWalletAddress = thisUser.getWallet();
                        
                        if (studentWalletAddress == null || studentWalletAddress.isEmpty()) {
                            Toast.makeText(getContext(), "Check-in unsuccessful\nStudent wallet not found", Toast.LENGTH_LONG).show();
                            return;
                        }

                        showMintingProgress(eName, reward);

                        // Mint tokens in background thread
                        new Thread(() -> {
                            try {
                                Log.d("EventCheckin", "Starting token minting...");
                                Log.d("EventCheckin", "Student wallet: " + studentWalletAddress);
                                Log.d("EventCheckin", "Tokens to mint: " + reward);
                                
                                // Mint tokens to student's wallet
                                ethereumManager.mintTokens(studentWalletAddress, BigInteger.valueOf(reward));
                                
                                // Update UI on main thread
                                requireActivity().runOnUiThread(() -> {
                                    // Add transaction to local database for record keeping
                                    Transaction trans = new Transaction(formattedDateTime, 1, uid, reward, checkInIdNum, "e");
                                    mysqliteopenhelper.addTransaction(trans);
                                    
                                    dismissMintingProgress();
                                    Toast.makeText(getContext(), 
                                        "Check-in successful!\nEvent: " + eName,
                                        Toast.LENGTH_SHORT).show();
                                        
                                    Log.d("EventCheckin", "Token minting completed successfully");
                                });
                                
                            } catch (Exception e) {
                                Log.e("EventCheckin", "Error minting tokens: " + e.getMessage());
                                requireActivity().runOnUiThread(() -> {
                                    dismissMintingProgress();
                                    Toast.makeText(getContext(), 
                                        "Check-in recorded but token minting failed. Please contact admin.", 
                                        Toast.LENGTH_LONG).show();
                                });
                            }
                        }).start();
                    }
                }
            } else {
                Toast.makeText(getContext(), "Scan Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 如果后续需要获取eventId的地方，可以通过调用getCheckInId()方法来获取
    public String getCheckInId() {
        return checkInId;
    }

    private void showMintingProgress(String eventName, int tokens) {
        if (progressDialog != null && progressDialog.isShowing()) { progressDialog.dismiss(); }
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Processing Transaction...");
        progressDialog.setMessage("Minting " + tokens + " HKUTokens to your blockchain wallet.\nThis may take a moment.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false); 
        progressDialog.show();
    }
    
    private void dismissMintingProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up progress dialog
        dismissMintingProgress();
    }

}
