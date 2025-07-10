package com.arvoice.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arvoice.databinding.ActivitySimCardManagerBinding;
import com.arvoice.model.CallLogItem;
import com.arvoice.model.CurrentUserModel;
import com.arvoice.networkinh.ApiClient;
import com.arvoice.networkinh.ApiInterface;
import com.arvoice.utils.Constant;
import com.arvoice.utils.ProgressUtils;
import com.arvoice.utils.Utils;

import org.json.JSONArray;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SimCardManagerActivity extends AppCompatActivity {

    private ActivitySimCardManagerBinding binding;
    private SharedPreferences prefs;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private List<CallLogItem> data = new ArrayList<>();
    private Context mContext;
    String userName;
    String callerPhone;
    String callerName;
    String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySimCardManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        getSavedToken();

        setupToolbar();
        requestPermissionsIfNeeded();

        initSwitchStates();
        setupSwitchListeners();
        setupEditButtons();
    }
    private void getSavedToken() {
        userName = prefs.getString("userName", "");
        authToken = prefs.getString("token", null); // second parameter is default value if not found
        Log.d("SIM_MANGER", userName);
    }

    private void setupToolbar() {
        binding.ivBack.setOnClickListener(view -> finish());
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionsIfNeeded() {
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_PHONE_NUMBERS,  // ✅ Required for getLine1Number()
                            Manifest.permission.READ_CALL_LOG
                    }, PERMISSION_REQUEST_CODE);
        } else {
            loadSimInfo(); // Permissions are already granted
        }
    }
    private void loadSimInfo() {
        SubscriptionManager sm = (SubscriptionManager) getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<SubscriptionInfo> simList = sm.getActiveSubscriptionInfoList();

        if (simList != null) {
            for (SubscriptionInfo info : simList) {
                int subId = info.getSubscriptionId();
                int slotIndex = info.getSimSlotIndex();
                String carrier = info.getCarrierName() != null ? info.getCarrierName().toString() : "Unknown";
                String number = info.getNumber() != null ? info.getNumber() : "Unknown";
                TelephonyManager tm = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
                        .createForSubscriptionId(subId);

                String line1Number = tm.getLine1Number();
                if (slotIndex == 0) {
                    String name = prefs.getString("SIM1_NAME", line1Number);
                    binding.sim1Number.setText(name);
                    binding.sim1Operator.setText(carrier);
                }
                if (slotIndex == 1) {
                    String name = prefs.getString("SIM2_NAME", line1Number);
                    binding.sim2Number.setText(name);
                    binding.sim2Operator.setText(carrier);
                }
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSimInfo();
            } else {
                Toast.makeText(this, "Permission denied. Cannot read SIM info or call logs.", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void initSwitchStates() {
        boolean sim1Enabled = prefs.getBoolean("SIM1_ENABLED", true);
        boolean sim2Enabled = prefs.getBoolean("SIM2_ENABLED", false);

        // Only one should be enabled
        if (sim1Enabled) {
            binding.sim1Switch.setChecked(true);
            binding.sim2Switch.setChecked(false);
        } else {
            binding.sim1Switch.setChecked(false);
            binding.sim2Switch.setChecked(false);
        }
    }

    private void setupSwitchListeners() {
        binding.sim1Switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setSimState(isChecked, binding.sim2Switch.isChecked());
        });
        binding.sim2Switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setSimState(binding.sim1Switch.isChecked(), isChecked);
        });

    }

    private void setSimState(boolean sim1, boolean sim2) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("SIM1_ENABLED", sim1);
        editor.putBoolean("SIM2_ENABLED", sim2);
        editor.apply();

        // Update UI to reflect exclusive toggle
        binding.sim1Switch.setChecked(sim1);
        binding.sim2Switch.setChecked(sim2);
    }
    private void setupEditButtons() {
        binding.sim1Edit.setOnClickListener(v -> showNameDialog("SIM1"));
        binding.sim2Edit.setOnClickListener(v -> showNameDialog("SIM2"));
    }

    private void showNameDialog(String simKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit SIM Name");

        final EditText input = new EditText(this);
        input.setHint("Enter name");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                SharedPreferences.Editor editor = prefs.edit();
                if (simKey.equals("SIM1")) {
                    editor.putString("SIM1_NAME", newName);
                    binding.sim1Number.setText(newName);
                } else {
                    editor.putString("SIM2_NAME", newName);
                    binding.sim2Number.setText(newName);
                }
                editor.apply();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


}