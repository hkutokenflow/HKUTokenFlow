package com.example.workshop1.Admin.Event;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.workshop1.Admin.Vendor.VendorItem;
import com.example.workshop1.R;
import com.example.workshop1.SQLite.Event;
import com.example.workshop1.SQLite.Mysqliteopenhelper;

import java.util.ArrayList;
import java.util.List;


public class EditEventsFragment extends Fragment {

    private ListView eventListView;
    private Button addButton;
    private Button genQrButton;
    private List<EventItem> eventList;
    private EventListAdapter adapter;
    private Mysqliteopenhelper mysqliteopenhelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_events, container, false);

        eventListView = view.findViewById(R.id.event_list_view);
        addButton = view.findViewById(R.id.btn_add);
        genQrButton = view.findViewById(R.id.btn_gen_qr);
        eventList = new ArrayList<>();

        //-------------------------ADD----------------------------------

        // ----------------------------SQLite-----------------------------------
        // -----------------------！！！！！！这里接数据库！！！！！！---------------------
        // ----------------------------Adapter也需要根据后端进行调整-----------------------------------
        // ###################注意：###################
        // 增删改查，这里只有增，删和改在adapter

        mysqliteopenhelper = new Mysqliteopenhelper(requireContext());
        Cursor cursor = mysqliteopenhelper.getEvents();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                int tokens = cursor.getInt(2);
                eventList.add(new EventItem(name, tokens));
            }
        }

        //-------------------EDIT和DELETE都在这边----------------------
        adapter = new EventListAdapter(getContext(), eventList);
        eventListView.setAdapter(adapter);

        addButton.setOnClickListener(v -> showAddEventDialog());

        genQrButton.setOnClickListener(v -> {
            // Create an Intent to open a web browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.qr-code-generator.com/"));
            startActivity(browserIntent);
        });

        return view;

        /* 测试用假数据
        eventList.add(new EventItem("Orientation Day", 20));
        eventList.add(new EventItem("Blockchain Talk", 35));
        eventList.add(new EventItem("Inno Show", 35));
        eventList.add(new EventItem("Career talk", 35));
        eventList.add(new EventItem("CS Talk", 35)); */

    }


    private void showAddEventDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_event, null);
        EditText nameInput = dialogView.findViewById(R.id.et_event_name);
        EditText tokenInput = dialogView.findViewById(R.id.et_token_count);
        Button confirmButton = dialogView.findViewById(R.id.btn_confirm);
        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        confirmButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String tokenStr = tokenInput.getText().toString().trim();

            if (name.isEmpty() || tokenStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int tokens = Integer.parseInt(tokenStr);
                Event event = new Event(name, tokens);
                long res = mysqliteopenhelper.addEvent(event);
                if (res != -1) {
                    Toast.makeText(requireContext(), "Event added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to add event.", Toast.LENGTH_SHORT).show();
                }

                eventList.add(new EventItem(name, tokens));
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Token amount must be a number", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }



}


