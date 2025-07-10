package com.arvoice.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arvoice.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private Context mContext;
    private ActivitySplashBinding binding;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    private SharedPreferences prefs;

    public static Intent makeIntent(Context context) {
        return new Intent(context, SplashActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                applyInit(); // Already granted
            }
        } else {
            applyInit(); // Not needed for < Android 13
        }
    }


    private void applyInit() {
        // Delay for 3 seconds (3000 milliseconds), then move to LoginActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String email = prefs.getString("email", "");
                if(email.isEmpty()){
                    startActivity(LoginActivity.makeIntent(mContext));
                    finish(); // Close SplashActivity
                }else{
                    startActivity(DashboardActivity.makeIntent(mContext));
                    finish(); // Close SplashActivity
                }

            }
        }, 1000); // 3-second delay
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied – show fallback UI or toast
            }
            applyInit();
        }
    }
}
