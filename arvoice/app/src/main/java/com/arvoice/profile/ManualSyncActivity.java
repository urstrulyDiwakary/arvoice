package com.arvoice.profile;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arvoice.adapter.CallLogAdapter;
import com.arvoice.databinding.ActivityManualSyncBinding;
import com.arvoice.model.CallLogItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ManualSyncActivity extends AppCompatActivity implements CallLogAdapter.ItemClick,CallLogAdapter.InfoClick {

    private ActivityManualSyncBinding binding;
    private SharedPreferences prefs;
    private List<CallLogItem> data = new ArrayList<>();
    private CallLogAdapter adapter;
    String userName;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManualSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        getSavedToken();

        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        setupToolbar();
        initView();
    }

    private void getSavedToken() {
        SharedPreferences prefs = mContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userName = prefs.getString("userName", "");
        Log.d("SIM_MANGER", userName);

    }
    private void setupToolbar() {
        binding.ivBack.setOnClickListener(view -> finish());
    }

    private void initView() {
        adapter = new CallLogAdapter(data,this,this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        checkPermissionAndLoad(false);
    }
    private void checkPermissionAndLoad(boolean missedOnly) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG}, 101);
        } else {
            loadCallLogs(missedOnly);
        }
    }
    @Override
    public void onItemClick(String number) {

    }

    @Override
    public void onInfoClick(String number) {

    }
    private int getEnabledSubscriptionId() {
        SubscriptionManager sm = (SubscriptionManager) getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return -1;
        }
        List<SubscriptionInfo> list = sm.getActiveSubscriptionInfoList();
        if (list == null) return -1;

        for (SubscriptionInfo info : list) {
            int slot = info.getSimSlotIndex();
            if (slot == 0 && prefs.getBoolean("SIM1_ENABLED", false)) {
                return info.getSubscriptionId();
            } else if (slot == 1 && prefs.getBoolean("SIM2_ENABLED", false)) {
                return info.getSubscriptionId();
            }
        }
        return -1;
    }

    private void loadCallLogs(boolean missedOnly) {
        data.clear();
        Map<Integer, Integer> simSlotToSubIdMap = new HashMap<>();

        SubscriptionManager sm = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
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
        List<SubscriptionInfo> list = sm.getActiveSubscriptionInfoList();

        if (list != null) {
            for (SubscriptionInfo info : list) {
                int slot = info.getSimSlotIndex();          // 0 = SIM1, 1 = SIM2
                int subId = info.getSubscriptionId();       // dynamic ID used in call log
                simSlotToSubIdMap.put(slot, subId);
            }
        }
        Cursor test = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        if (test != null) {
            for (String col : test.getColumnNames()) {
                Log.d("CALL_LOG_COL", col);
            }
            test.close();
        }
        // Assume you want SIM 1 calls (slot 0)
        int sim1SubId = simSlotToSubIdMap.get(0);

        String[] projection = {
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                "subscription_id"
        };

        String selection = "subscription_id = ?";
        String[] selectionArgs = new String[]{String.valueOf(sim1SubId)};

        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                CallLog.Calls.DATE + " DESC"
        );


        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int simIndex = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);

            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int phoneAccountIndex = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);

            while (cursor.moveToNext()) {

                String phoneAccountId = phoneAccountIndex != -1 ? cursor.getString(phoneAccountIndex) : null;

                String simInfo = "SIM";
                if (simIndex != -1) {
                    String rawSimId = cursor.getString(simIndex);
                    if (rawSimId != null) {
                        rawSimId = rawSimId.toLowerCase();
                        simInfo = rawSimId.contains("1") ? "SIM 1"
                                : rawSimId.contains("2") ? "SIM 2"
                                : "SIM";
                    }
                }


                String contactName = nameIndex != -1 ? cursor.getString(nameIndex) : "Unknown";
                String contactPhoneNo = numberIndex != -1 ? cursor.getString(numberIndex) : "";
                int type = typeIndex != -1 ? cursor.getInt(typeIndex) : 0;
                long dateMillis = dateIndex != -1 ? cursor.getLong(dateIndex) : 0;
                int durationSec = durationIndex != -1 ? cursor.getInt(durationIndex) : 0;

                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                String startTime = isoFormat.format(new Date(dateMillis));

                Date endDate = new Date(dateMillis + (durationSec * 1000L));
                String endTime = isoFormat.format(endDate);

                String callType;
                boolean answered = false;
                boolean unanswered = false;
                switch (type) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        callType = "Outgoing";
                        answered = true;
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        callType = "Incoming";
                        answered = durationSec > 0;
                        unanswered = durationSec == 0;
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        callType = "Missed";
                        unanswered = true;
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        callType = "Rejected";
                        unanswered = true;
                        break;
                    case CallLog.Calls.BLOCKED_TYPE:
                        callType = "Blocked";
                        unanswered = true;
                        break;
                    default:
                        callType = "Unknown";
                        break;
                }
                String callStatus = answered ? "completed" : "uncomplete";

                data.add(new CallLogItem(
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
                        "",
                        ""
                ));
            }
            Log.d("TAG", "loadCallLogs() called with: missedOnly = [" + data.size() + "]");
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }
}