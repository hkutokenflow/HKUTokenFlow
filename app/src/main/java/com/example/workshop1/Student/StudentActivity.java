package com.example.workshop1.Student;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.example.workshop1.Login.LoginActivity;
import com.example.workshop1.R;
import com.example.workshop1.Student.EventCheckin.EventCheckinFragment;
import com.example.workshop1.Student.RedeemReward.RedeemRewardFragment;
import com.example.workshop1.Student.StudentHome.StudentHomeFragment;
import com.example.workshop1.Student.TokenFlow.TokenFlowFragment;
import com.example.workshop1.Student.YourReward.StudentRewardFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class StudentActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Student");

        // 设置导航图标
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 默认加载 HomeFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new StudentHomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selectedFragment = new StudentHomeFragment();
                toolbar.setTitle("Student Home");
            } else if (id == R.id.nav_event_checkin) {
                selectedFragment = new EventCheckinFragment();
                toolbar.setTitle("Events Check-in");
            } else if (id == R.id.nav_redeem_rewards) {
                selectedFragment = new RedeemRewardFragment();
                toolbar.setTitle("Redeem Rewards");
            } else if (id == R.id.nav_student_rewards) {
                selectedFragment = new StudentRewardFragment();
                toolbar.setTitle("Your Rewards");
            } else if (id == R.id.nav_hkuTokenflow) {
                selectedFragment = new TokenFlowFragment();
                toolbar.setTitle("HKU TokenFlow");
            } else if (id == R.id.nav_logout) {
                // 处理退出登录
                Intent intent = new Intent(StudentActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // 清空返回栈，防止按返回键回到student页面
                startActivity(intent);
                finish(); // 结束当前Activity
                return true;
            }


            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


    }
}