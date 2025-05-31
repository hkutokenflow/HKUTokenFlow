package com.example.workshop1.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workshop1.R;
import com.example.workshop1.SQLite.Mysqliteopenhelper;
import com.example.workshop1.SQLite.User;
import com.example.workshop1.Utils.PasswordEncryption;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText et_forget,et_pwd,et_equal;
    private ImageView iv_eye2,iv_eye3;
    private CheckBox cb_accept;
    private Mysqliteopenhelper mysqliteopenhelper;
    private int Visiable1=0,Visiable2=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        et_forget=findViewById(R.id.et_forget_name);
        et_pwd=findViewById(R.id.et_forget_password);
        et_equal=findViewById(R.id.et_forget_equal_password);

        iv_eye2=findViewById(R.id.iv_eye2);
        iv_eye3=findViewById(R.id.iv_eye3);

        cb_accept=(CheckBox)findViewById(R.id.accept_policy);

        mysqliteopenhelper=new Mysqliteopenhelper(this);

        //----------------------------SQL----------------------
        mysqliteopenhelper=new Mysqliteopenhelper(this);

    }

    //--------------------------------------change_password--------------------------------------

    public void change_password(View view){
        String name = et_forget.getText().toString();
        String pwd = et_pwd.getText().toString();
        String equal = et_equal.getText().toString();

        if (!pwd.equals(equal)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cb_accept.isChecked()) {
            Toast.makeText(this, "Please agree to the HKU Privacy Policy", Toast.LENGTH_SHORT).show();
            return;
        }

        // 加密密码
        String encryptedPassword = PasswordEncryption.encrypt(pwd);
        if (encryptedPassword == null) {
            Toast.makeText(this, "Password encryption failed", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(name, encryptedPassword, "", "");
        long res = mysqliteopenhelper.addUser(user);
        if (res != -1) {
            Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Password change failed", Toast.LENGTH_SHORT).show();
        }
    }


    //监听密码是否可见
    public void Isvisiable2(View view){
        if(Visiable1==0){
            et_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//可见
            iv_eye2.setImageResource(R.drawable.baseline_visibility_24);
            Visiable1=1;
        }
        else{
            et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());//不可见
            iv_eye2.setImageResource(R.drawable.baseline_visibility_off_24);
            Visiable1=0;
        }
    }

    //监听密码是否可见
    public void Isvisiable3(View view){
        if(Visiable2==0){
            et_equal.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//可见
            iv_eye3.setImageResource(R.drawable.baseline_visibility_24);
            Visiable2=1;
        }
        else{
            et_equal.setTransformationMethod(PasswordTransformationMethod.getInstance());//不可见
            iv_eye3.setImageResource(R.drawable.baseline_visibility_off_24);
            Visiable2=0;
        }
    }
}