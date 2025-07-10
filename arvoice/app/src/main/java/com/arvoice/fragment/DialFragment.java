package com.arvoice.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arvoice.R;
import com.arvoice.activity.AddLeadsActivity;
import com.arvoice.activity.ContactDetailsActivity;
import com.arvoice.adapter.CallLogAdapter;
import com.arvoice.databinding.ActivityDialerBinding;
import com.arvoice.model.CallLogItem;
import com.arvoice.model.CurrentUserModel;
import com.arvoice.model.SummaryModel;
import com.arvoice.networkinh.ApiClient;
import com.arvoice.networkinh.ApiInterface;
import com.arvoice.utils.Constant;
import com.arvoice.utils.ProgressUtils;
import com.arvoice.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;

public class DialFragment extends Fragment implements CallLogAdapter.ItemClick,CallLogAdapter.InfoClick{
    private ActivityDialerBinding binding;
    private Context mContext;
    private StringBuilder phoneNumber = new StringBuilder();

    // Dialer button IDs
    private final int[] buttonIds = {
            R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnStar, R.id.btn0,R.id.btnHash
    };
    private static final int REQUEST_CALL_LOG_PERMISSION = 101;
    private List<CallLogItem> data = new ArrayList<>();
    private CallLogAdapter adapter;
    boolean sim1Enabled;
    boolean sim2Enabled;
    String userName;
    String authToken;
    String callerPhone;
    String callerName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialerBinding.inflate(getLayoutInflater());
        mContext = getActivity();
        Log.d("DEBUG", "onStart() called");
        getSavedToken();
        setupKeypad(); // ← Setup listeners
        setupCallButtons(); // ← SIM buttons

        initView();
        return binding.getRoot();
    }
    private void callGetCurrentUser() {
        ProgressUtils.showProgressDialog(requireActivity());
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your actual method to get token
        Log.e("Dial","****"+token);

        retrofit2.Call<CurrentUserModel> call = apiInterface.getCurrentUser(token);
        call.enqueue(new retrofit2.Callback<CurrentUserModel>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<CurrentUserModel> call, @NonNull retrofit2.Response<CurrentUserModel> response) {
                ProgressUtils.hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    CurrentUserModel userModel  = response.body();
                    callerPhone = userModel.getMobile();
                    callerName = userModel.getUsername();
                    Log.e("Dashboard", "User Response" + userModel.getMobile());

                } else {
                    Log.e("Dashboard", "1234****" + "Error");
                    Utils.showToast(Constant.API_CODES.ON_FAILURE_MESSAGE);
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<CurrentUserModel> call, @NonNull Throwable t) {
                ProgressUtils.hideProgressDialog();
                t.printStackTrace();
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL_LOG_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.indicatorAll.setVisibility(View.VISIBLE);
                binding.indicatorMissed.setVisibility(View.GONE);

                binding.tabAll.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                binding.indicatorAll.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
                binding.indicatorMissed.setBackgroundColor(ContextCompat.getColor(mContext,R.color.gray));
                binding.tabMissed.setTextColor(ContextCompat.getColor(mContext, R.color.gray));

                loadCallLogs(false); // Load all logs
            } else {
                Toast.makeText(getContext(), "Permission denied to read call logs", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Setup keypad buttons
    private void setupKeypad() {
        for (int id : buttonIds) {
            Button btn = binding.getRoot().findViewById(id); // Use root view
            btn.setOnClickListener(v -> {
                String key = ((Button) v).getText().toString().substring(0, 1); // Only the digit
                phoneNumber.append(key);
                binding.tvPhoneNumber.setText(phoneNumber.toString()); // Update TextView
            });
        }
    }

    // Setup SIM call buttons
    private void setupCallButtons() {
        binding.tvPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        binding.btnSim1.setOnClickListener(v -> makeCall(binding.tvPhoneNumber.getText().toString(), 0));
        binding.btnSim2.setOnClickListener(v -> makeCall(binding.tvPhoneNumber.getText().toString(), 1));

    }
//
    private void initView() {
        binding.btnAddLeads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = binding.tvPhoneNumber.getText().toString();
                Log.e("Dia","number***"+number);
                startActivity(new Intent(mContext, AddLeadsActivity.class).putExtra("number",number));
            }
        });
        binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        binding.searchView.setOnSearchClickListener(v -> {
            binding.tvHeading.setVisibility(View.GONE); // Hide title on expand
        });
        binding.searchView.setOnCloseListener(() -> {
            binding.tvHeading.setVisibility(View.VISIBLE); // Show title on collapse
            return false;
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterList(newText);
                return true;
            }
        });

        adapter = new CallLogAdapter(data,this,this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        binding.recyclerView.setAdapter(adapter);

        binding.layoutTabAll.setOnClickListener(v -> {
            binding.indicatorAll.setVisibility(View.VISIBLE);
            binding.indicatorMissed.setVisibility(View.GONE);

            binding.tabAll.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            binding.indicatorAll.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
            binding.indicatorMissed.setBackgroundColor(ContextCompat.getColor(mContext,R.color.gray));
            binding.tabMissed.setTextColor(ContextCompat.getColor(mContext, R.color.gray));

            loadCallLogs(false); // Load all logs
        });
        binding.layoutTabMissed.setOnClickListener(v -> {
            binding.indicatorAll.setVisibility(View.GONE);
            binding.indicatorMissed.setVisibility(View.VISIBLE);

            binding.tabAll.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
            binding.indicatorMissed.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
            binding.indicatorAll.setBackgroundColor(ContextCompat.getColor(mContext,R.color.gray));
            binding.tabMissed.setTextColor(ContextCompat.getColor(mContext, R.color.black));

            loadCallLogs(true); // Load missed logs only
        });
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = binding.tvPhoneNumber.getText().toString();
                if (!currentText.isEmpty()) {
                    // Remove last digit
                    String updatedText = currentText.substring(0, currentText.length() - 1);
                    binding.tvPhoneNumber.setText(updatedText);
                    binding.tvPhoneNumber.setSelection(updatedText.length()); // move cursor to end
                    phoneNumber.setLength(updatedText.length()); // update your StringBuilder if needed
                }
                binding.tvPhoneNumber.clearFocus();
            }
        });

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy != 0 && binding.llKeyPad.getVisibility() == View.VISIBLE) {
                    binding.llKeyPad.setVisibility(View.GONE);
                    binding.fabShowKeypad.show();
                }
            }
        });

        binding.fabShowKeypad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.llKeyPad.setVisibility(View.VISIBLE);
                binding.fabShowKeypad.hide();
            }
        });

    }

    private void checkPermissionAndLoad(boolean missedOnly) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Marshmallow and above
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CALL_LOG)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_CALL_LOG_PERMISSION);
            } else {
                Log.d("DEBUG", "checkPermissionAndLoad() called");

                loadCallLogs(missedOnly);
            }
        } else {
            Log.d("DEBUG", "checkPermissionAndLoad() called");

            // For API levels below 23, permissions are granted at install time
            loadCallLogs(missedOnly);
        }
    }


    private void loadCallLogs(boolean missedOnly) {
        data.clear();

        String[] projection = {
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.PHONE_ACCOUNT_ID
        };

        String selection = missedOnly
                ? CallLog.Calls.TYPE + " = " + CallLog.Calls.MISSED_TYPE
                : null;

        Cursor cursor = getActivity().getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                null,
                CallLog.Calls.DATE + " DESC"
        );

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int simIndex = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);

            Calendar todayStart = Calendar.getInstance();
            todayStart.set(Calendar.HOUR_OF_DAY, 0);
            todayStart.set(Calendar.MINUTE, 0);
            todayStart.set(Calendar.SECOND, 0);
            todayStart.set(Calendar.MILLISECOND, 0);
            long todayStartMillis = todayStart.getTimeInMillis();

            while (cursor.moveToNext()) {
                long dateMillis = dateIndex != -1 ? cursor.getLong(dateIndex) : 0;
                if (dateMillis < todayStartMillis) continue;

                String contactName = "Unknown";
                if (nameIndex != -1) {
                    String rawName = cursor.getString(nameIndex);
                    if (rawName != null && !rawName.trim().isEmpty()) {
                        contactName = rawName;
                    }
                }

                String contactPhoneNo = numberIndex != -1 ? cursor.getString(numberIndex) : "";
                int type = typeIndex != -1 ? cursor.getInt(typeIndex) : 0;
                int durationSec = durationIndex != -1 ? cursor.getInt(durationIndex) : 0;

                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                String startTime = isoFormat.format(new Date(dateMillis));
                String endTime = isoFormat.format(new Date(dateMillis + durationSec * 1000L));

                // Detect SIM
                String simInfo = "SIM";
                if (simIndex != -1) {
                    String rawSimId = cursor.getString(simIndex);
                    if (rawSimId != null) {
                        rawSimId = rawSimId.toLowerCase();
                        if (rawSimId.contains("1")) {
                            simInfo = "SIM 1";
                        } else if (rawSimId.contains("2")) {
                            simInfo = "SIM 2";
                        }
                    }
                }

                String callType;
                boolean answered, unanswered;

                switch (type) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        callType = "Outgoing";
                        answered = durationSec > 0;
                        unanswered = durationSec == 0;
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        callType = "Incoming";
                        answered = durationSec > 0;
                        unanswered = durationSec == 0;
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        callType = "Missed";
                        answered = false;
                        unanswered = true;
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        callType = "Rejected";
                        answered = false;
                        unanswered = true;
                        break;
                    case CallLog.Calls.BLOCKED_TYPE:
                        callType = "Blocked";
                        answered = false;
                        unanswered = true;
                        break;
                    default:
                        callType = "Unknown";
                        answered = false;
                        unanswered = true;
                        break;
                }

                String callStatus = answered ? "completed" : "incomplete";

                CallLogItem item = new CallLogItem(
                        userName,
                        contactName,
                        contactPhoneNo,
                        simInfo,
                        startTime,
                        endTime,
                        callType,
                        callStatus,
                        durationSec,
                        answered,
                        unanswered,
                        callerPhone,
                        callerName
                );

                data.add(item); // Store everything for today
            }

            cursor.close();

            if (adapter != null && !data.isEmpty()) {
                adapter.updateFullData(data);
                syncFilteredLogs();
            }


        }
    }

    private void syncFilteredLogs() {
        SharedPreferences prefs = getContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean sim1Enabled = prefs.getBoolean("SIM1_ENABLED", false);
        boolean sim2Enabled = prefs.getBoolean("SIM2_ENABLED", false);

        Log.i("SIM!",""+sim1Enabled);
        Log.i("SIM2",""+sim2Enabled);

        JSONArray syncArray = new JSONArray();
        if (!sim1Enabled && !sim2Enabled) {
            Log.d("SYNC_LOGS", "No SIM enabled — nothing to sync.");
            return;
        }
        for (CallLogItem item : data) {
            String sim = item.simInfo; // or item.getSim()

            // ✅ If both SIMs are enabled, add SIM 1 and SIM 2 calls only
            if (sim1Enabled && sim2Enabled) {
                if ("SIM 1".equals(sim) || "SIM 2".equals(sim)) {
                    syncArray.put(item.toJson());
                }
            }
            // ✅ If only SIM 1 is enabled
            else if (sim1Enabled && "SIM 1".equals(sim)) {
                syncArray.put(item.toJson());
            }
            // ✅ If only SIM 2 is enabled
            else if (sim2Enabled && "SIM 2".equals(sim)) {
                syncArray.put(item.toJson());
            }
        }

        if (syncArray.length() > 0) {
            Log.d("SYNC_LOGS", syncArray.toString());
            // callSync(syncArray);
        } else {
            Log.d("SYNC_LOGS", "No SIM enabled or no matching call logs.");
        }
    }


    private void getSavedToken() {
        Log.d("DEBUG", "getSavedToken() called");

        SharedPreferences prefs = mContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = prefs.getString("token", null); // second parameter is default value if not found
        userName = prefs.getString("userName", "");
        callGetCurrentUser();
        Log.d("DEBUG", "Token: " + authToken + ", UserName: " + userName);

        checkPermissionAndLoad(false);
        Log.d("SIM_MANGER", String.valueOf(sim1Enabled));
        Log.d("SIM_MANGER", userName);
    }

    private void callSync(JSONArray jsonArray){
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(jsonArray.toString(), JSON);

        Request request = new Request.Builder()
                .url("http://13.201.184.185/api/call-records/")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", "JSESSIONID=C42E39DC299661029E3839B183D77AA1")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("CallLogsPOST", "Request Failed: " + e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Log.d("CallLogsPOST", "Success: " + response.body().toString());
                } else {
                    Log.e("CallLogsPOST", "Error: " + response.code() + " -> " + response.message());
                }
            }

        });

    }
    private void makeCall(String number, int simSlot) {
        if (number.isEmpty()) {
            Utils.showToast("Please enter a number");
            return;
        }

        TelecomManager telecomManager = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        SubscriptionManager subscriptionManager = (SubscriptionManager) mContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
            return;
        }


        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (subscriptionInfoList != null && subscriptionInfoList.size() > simSlot) {
            SubscriptionInfo subscriptionInfo = subscriptionInfoList.get(simSlot);

            PhoneAccountHandle phoneAccountHandle = null;
            for (PhoneAccountHandle handle : telecomManager.getCallCapablePhoneAccounts()) {
                if (handle.getId().contains(String.valueOf(subscriptionInfo.getSubscriptionId()))) {
                    phoneAccountHandle = handle;
                    break;
                }
            }

            if (phoneAccountHandle != null) {
                Bundle extras = new Bundle();
                extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);

                Uri uri = Uri.fromParts("tel", number, null);
                telecomManager.placeCall(uri, extras);
            } else {
                Utils.showToast("Unable to find SIM slot.");
            }
        } else {
            Utils.showToast("Invalid SIM slot");
        }
    }


    @Override
    public void onItemClick(String number) {
        makeCall(number,0);
    }

    @Override
    public void onInfoClick(String number) {
        startActivity(new Intent(mContext, ContactDetailsActivity.class).putExtra("number",number));

    }
}
