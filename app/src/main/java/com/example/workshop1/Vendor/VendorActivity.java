package com.example.workshop1.Vendor;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.workshop1.Login.LoginActivity;
import com.example.workshop1.R;
import com.example.workshop1.Vendor.VendorHome.VendorHomeFragment;
import com.example.workshop1.Vendor.Voucher.EditVouchersFragment;
import com.example.workshop1.Vendor.changePassword.changeVendorPasswordFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class VendorActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");

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
                    .replace(R.id.fragment_container, new VendorHomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selectedFragment = new VendorHomeFragment();
                toolbar.setTitle("Home");
            } else if (id == R.id.nav_edit_vouchers) {
                selectedFragment = new EditVouchersFragment();
                toolbar.setTitle("Edit Vouchers");
            }
            else if(id == R.id.nav_changepwd){
                selectedFragment = new changeVendorPasswordFragment();
                toolbar.setTitle("Change Passwords");
            }
            else if(id == R.id.nav_logout){
                // log out
                Intent intent = new Intent(VendorActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // 清空返回栈，防止按返回键回到vendor页面
                startActivity(intent);
                finish(); // end this Activity
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