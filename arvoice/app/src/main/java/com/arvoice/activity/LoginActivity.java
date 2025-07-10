package com.arvoice.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arvoice.R;
import com.arvoice.databinding.ActLoginBinding;
import com.arvoice.databinding.ActivityLoginBinding;
import com.arvoice.databinding.ActivitySplashBinding;
import com.arvoice.model.LoginRequest;
import com.arvoice.model.LoginResponse;
import com.arvoice.networkinh.ApiClient;
import com.arvoice.networkinh.ApiInterface;
import com.arvoice.utils.Utils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private Context mContext;
    private ActLoginBinding binding;
    String token;

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        FirebaseApp.initializeApp(this);
        listener();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM_TOKEN", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM token
                    token = task.getResult();
                    Log.d("FCM_TOKEN", "Token: " + token);

                    // Send token to your server
                });

    }

    @SuppressLint("ClickableViewAccessibility")
    public void listener(){
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (isValidInput(email, password)) {
                callLogin(email,password);
            }
        });
        binding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        binding.ivTogglePassword.setImageResource(R.drawable.eye_on);
        binding.ivTogglePassword.setOnClickListener(new View.OnClickListener() {
            private boolean isPasswordVisible = true; // ✅ Initially visible

            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.ivTogglePassword.setImageResource(R.drawable.eye_off);
                } else {
                    binding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.ivTogglePassword.setImageResource(R.drawable.eye_on);
                }
                binding.etPassword.setSelection(binding.etPassword.length()); // move cursor to end
                isPasswordVisible = !isPasswordVisible;
            }
        });


    }
    private boolean isValidInput(String email, String password) {
        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            binding.etEmail.requestFocus();
            return false;
        }
//        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            binding.etEmail.setError("Enter a valid email");
//            binding.etEmail.requestFocus();
//            return false;
//        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            binding.etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void callLogin(String email,String password) {
        String fcm = token;
        if (fcm != null) {
            Log.i("FCM", fcm);
        } else {
            fcm = "sjksjskjskjskjsksjksjksjksjks";
            Log.e("FCM", "FCM token is null");
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", email);
            jsonObject.put("password", password);
            jsonObject.put("fcmToken", fcm);
            Log.i("Login JSON", jsonObject.toString());

        }catch (Exception e){
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        Call<LoginResponse> call = apiInterface.login(requestBody);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d("Login***", "Response: " + loginResponse.toString());

                    if (loginResponse.getToken() != null) {
                        // Save token
                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("role", loginResponse.getRole());
                        editor.putString("userId", loginResponse.getUserId());
                        editor.putString("token", loginResponse.getToken());
                        editor.putString("email", loginResponse.getEmail());
                        editor.putBoolean("SIM1_ENABLED", false);
                        editor.putBoolean("SIM2_ENABLED", false);
                        editor.putString("userName", loginResponse.getUsername());
                        Log.e("Add Lead","****"+loginResponse.getUsername());

                        editor.apply();

                        animateCarAndNavigate();
                        // After animation, open next screen
                        // proceed to next screen
                    } else {
                        Toast.makeText(getApplicationContext(), loginResponse.getError(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        JSONObject errorBody = new JSONObject(response.errorBody().string());
                        Log.d("Login***", "Response: " + errorBody);

                        Toast.makeText(getApplicationContext(), errorBody.optString("error", "Login failed"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                Utils.showToast("Error: " + t.getMessage());
            }
        });

    }

    private void animateCarAndNavigate() {
        // Calculate destination X (right end)
        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        float targetX = screenWidth - binding.carImage.getWidth() - 50;

        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.carImage, "translationX", targetX);
        animator.setDuration(1500); // 1.5 seconds
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish(); // optiona
            }
        });

        animator.start();
    }

}
