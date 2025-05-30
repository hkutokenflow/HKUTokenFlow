package com.example.workshop1.Login;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workshop1.R;

import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailVerifyActivity extends AppCompatActivity {

    private EditText etEmail, etCode;
    private String sentCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        etEmail = findViewById(R.id.et_email);
        etCode = findViewById(R.id.et_verify_code);

        findViewById(R.id.btn_send_code).setOnClickListener(v -> sendCode());
        findViewById(R.id.btn_verify).setOnClickListener(v -> verifyCode());
    }

    private void sendCode() {
        String email = etEmail.getText().toString().trim();
        if (!isHKUEmail(email)) {
            Toast.makeText(this, "Must use HKU email", Toast.LENGTH_SHORT).show();
            return;
        }

        sentCode = generateCode();

        new Thread(() -> {
            try {
                EmailSender.sendEmail(email, "Your Verification Code", "Your code is: " + sentCode);
                runOnUiThread(() ->
                        Toast.makeText(this, "Code sent!", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }

    private void verifyCode() {
        String inputCode = etCode.getText().toString().trim();
        if (inputCode.equals(sentCode)) {
            Toast.makeText(this, "Verification successful", Toast.LENGTH_SHORT).show();
            // 可以跳转回注册页面，或者设置一个“认证通过”的标志
        } else {
            Toast.makeText(this, "Code incorrect", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isHKUEmail(String email) {
        return email.endsWith("@connect.hku.hk") || email.endsWith("@hku.hk");
    }

    private String generateCode() {
        Random rand = new Random();
        int code = 100000 + rand.nextInt(900000); // 6位数
        return String.valueOf(code);
    }
}

