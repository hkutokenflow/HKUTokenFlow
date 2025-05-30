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

import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.Transaction;
import com.example.workshop1.SQLite.User;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_checkin, container, false);

        eventListView = view.findViewById(R.id.student_event_list_view);
        scanBtn = view.findViewById(R.id.btn_scan_qr);
        eventList = new ArrayList<>();

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

                        // get event reward
                        int reward = mysqliteopenhelper.getEventReward(checkInIdNum);

                        Transaction trans = new Transaction(formattedDateTime, 1, uid, reward, checkInIdNum, "e");
                        mysqliteopenhelper.addTransaction(trans);

                        String eName = mysqliteopenhelper.getEventName(checkInIdNum);

                        Toast.makeText(getContext(), "Check-in successful!\nEvent: " + eName, Toast.LENGTH_LONG).show();
                    }
                }

                // 此时，eventId就已经存储了，可以进行后续的数据库查询操作等
                // SQL查询，调用网络接口查询eventId是否有效，或是签到操作等。
            } else {
                Toast.makeText(getContext(), "Scan Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 如果后续需要获取eventId的地方，可以通过调用getCheckInId()方法来获取
    public String getCheckInId() {
        return checkInId;
    }

}
