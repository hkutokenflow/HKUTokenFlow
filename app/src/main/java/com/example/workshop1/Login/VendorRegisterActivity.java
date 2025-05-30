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
import com.example.workshop1.SQLite.VendorApproval;

import java.util.Random;

public class VendorRegisterActivity extends AppCompatActivity {

    private EditText et_name, et_pwd, et_equal, et_verifyCode;
    private ImageView iv_eye2, iv_eye3, iv_showCode;
    private CheckBox cb_accept;
    private Mysqliteopenhelper mysqliteopenhelper;
    private int Visiable1 = 0, Visiable2 = 0;
    private String realCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_register);
        
        et_name = findViewById(R.id.et_register_username);
        et_pwd = findViewById(R.id.et_register_password);
        et_equal = findViewById(R.id.et_equal_password);
        et_verifyCode = findViewById(R.id.et_verify_code);
        iv_eye2 = findViewById(R.id.iv_eye2);
        iv_eye3 = findViewById(R.id.iv_eye3);
        iv_showCode = findViewById(R.id.iv_showCode);
        cb_accept = findViewById(R.id.accept_policy);

        mysqliteopenhelper = new Mysqliteopenhelper(this);

        // 初始化验证码
        iv_showCode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
    }

    // 点击验证码图片刷新
    public void changenumber(View view) {
        iv_showCode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
    }

    private String generateUniqueUsername(String name) {
        // 将name转换为小写并移除空格
        String base = name.toLowerCase().replaceAll("\\s+", "");
        // 生成3位随机数
        Random random = new Random();
        int randomNum = random.nextInt(900) + 100; // 100-999
        return base + randomNum;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        
        // 确保至少包含一个大写字母
        password.append(chars.substring(0, 26).charAt(random.nextInt(26)));
        // 确保至少包含一个小写字母
        password.append(chars.substring(26, 52).charAt(random.nextInt(26)));
        // 确保至少包含一个数字
        password.append(chars.substring(52, 62).charAt(random.nextInt(10)));
        // 确保至少包含一个特殊字符
        password.append(chars.substring(62).charAt(random.nextInt(8)));
        
        // 添加更多随机字符使密码长度达到8位
        for (int i = 4; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // 打乱密码字符顺序
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }

    public void register_newuser(View view) {
        String name = et_name.getText().toString().trim();
        String pwd = et_pwd.getText().toString();
        String equal = et_equal.getText().toString();
        String inputCode = et_verifyCode.getText().toString().toLowerCase();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!inputCode.equals(realCode)) {
            Toast.makeText(this, "Verification code error!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPasswordValid(pwd)) {
            return;
        }

        if (!pwd.equals(equal)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cb_accept.isChecked()) {
            Toast.makeText(this, "Please agree to the HKU Privacy Policy", Toast.LENGTH_SHORT).show();
            return;
        }

        // 生成用户名，使用用户输入的密码
        String username = generateUniqueUsername(name);

        // 创建vendor审批申请，使用用户输入的密码
        VendorApproval vendorApproval = new VendorApproval(username, pwd, name);
        long res = mysqliteopenhelper.addVendorApproval(vendorApproval);
        
        if (res != -1) {
            Toast.makeText(this, "Registration submitted successfully! Please wait for admin approval.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPasswordValid(String password) {
        // 至少6位，必须同时包含大小写字母，数字和特殊符号
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasNumber = true;
            else hasSpecial = true;
        }
        if (!hasUpper || !hasLower || !hasNumber || !hasSpecial) {
            Toast.makeText(this, "Password must contain uppercase, lowercase, number and special character", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void Isvisiable1(View view) {
        if(Visiable1==0){
            et_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            iv_eye2.setImageResource(R.drawable.baseline_visibility_24);
            Visiable1=1;
        }
        else{
            et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            iv_eye2.setImageResource(R.drawable.baseline_visibility_off_24);
            Visiable1=0;
        }
    }

    public void Isvisiable2(View view) {
        if(Visiable2==0){
            et_equal.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            iv_eye3.setImageResource(R.drawable.baseline_visibility_24);
            Visiable2=1;
        }
        else{
            et_equal.setTransformationMethod(PasswordTransformationMethod.getInstance());
            iv_eye3.setImageResource(R.drawable.baseline_visibility_off_24);
            Visiable2=0;
        }
    }
} 