package com.example.workshop1.Vendor.changePassword;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.Login.EmailSender;
import com.example.workshop1.SQLite.User;

import java.util.Random;

public class changeVendorPasswordFragment extends Fragment {

    private EditText et_pwd, et_equal;
    private ImageView iv_eye2, iv_eye3;
    private Button btn_confirm;
    private Mysqliteopenhelper mysqliteopenhelper;
    private int Visiable1 = 0, Visiable2 = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_vendor_password, container, false);

        et_pwd = view.findViewById(R.id.et_change_password);
        et_equal = view.findViewById(R.id.et_equal_password);
        iv_eye2 = view.findViewById(R.id.iv_eye2);
        iv_eye3 = view.findViewById(R.id.iv_eye3);
        btn_confirm = view.findViewById(R.id.btn_confirm);

        mysqliteopenhelper = new Mysqliteopenhelper(requireContext());

        btn_confirm.setOnClickListener(v -> showConfirmDialog());
        iv_eye2.setOnClickListener(this::Isvisiable2);
        iv_eye3.setOnClickListener(this::Isvisiable3);

        return view;
    }

    //----------------------------------change_password----------------------------------
    private void showConfirmDialog() {
        String pwd = et_pwd.getText().toString();
        String equal = et_equal.getText().toString();

        if (!isPasswordValid(pwd)) {
            return;
        }

        if (!pwd.equals(equal)) {
            Toast.makeText(requireContext(), "Two time passwords are different", Toast.LENGTH_SHORT).show();
            return;
        }

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirm Change")
                .setMessage("Are you sure you want to change your password?")
                .setPositiveButton("Yes", (dialog, which) -> change_password())
                .setNegativeButton("Cancel", null)
                .show();
    }
    public void change_password() {
        String pwd = et_pwd.getText().toString();
        String equal = et_equal.getText().toString();

        //------------------------------------SQL---------------------------------------
        User thisUser = (User) requireActivity().getIntent().getSerializableExtra("userObj");
        mysqliteopenhelper = new Mysqliteopenhelper(requireContext());
        mysqliteopenhelper.editVendorPwd(thisUser.getUsername(), pwd);
        //-----------------------------Update Password here---------------------------------------

        // TODO: update password in database
        Toast.makeText(requireContext(), "Password changed successfully!", Toast.LENGTH_SHORT).show();
    }


    private boolean isHKUEmail(String email) {
        return email.endsWith("@connect.hku.hk") || email.endsWith("@hku.hk");
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            Toast.makeText(requireContext(), "Password must include at least one lowercase letter", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            Toast.makeText(requireContext(), "Password must include at least one number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void Isvisiable2(View view) {
        if (Visiable1 == 0) {
            et_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            iv_eye2.setImageResource(R.drawable.baseline_visibility_24);
            Visiable1 = 1;
        } else {
            et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            iv_eye2.setImageResource(R.drawable.baseline_visibility_off_24);
            Visiable1 = 0;
        }
    }

    public void Isvisiable3(View view) {
        if (Visiable2 == 0) {
            et_equal.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            iv_eye3.setImageResource(R.drawable.baseline_visibility_24);
            Visiable2 = 1;
        } else {
            et_equal.setTransformationMethod(PasswordTransformationMethod.getInstance());
            iv_eye3.setImageResource(R.drawable.baseline_visibility_off_24);
            Visiable2 = 0;
        }
    }
}
