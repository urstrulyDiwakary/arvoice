package com.arvoice.profile;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.arvoice.databinding.ActivityUserDetailBinding;

public class UserDetailActivity extends AppCompatActivity {

    private ActivityUserDetailBinding binding;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        setupToolbar();

        String role = prefs.getString("role", "");
        String userId = prefs.getString("userId", "");

        String email = prefs.getString("email", "");
        String userName = prefs.getString("userName", "");

        binding.tvUserName.setText(userName);
        binding.tvEmail.setText(email);
        binding.tvRole.setText(role);
        binding.tvUserId.setText(userId);

    }

    private void setupToolbar() {
        binding.ivBack.setOnClickListener(view -> finish());
    }
}