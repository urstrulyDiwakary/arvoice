package com.arvoice.activity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arvoice.adapter.CallDetailsAdapter;
import com.arvoice.databinding.ActivityContactDetailsBinding;
import com.arvoice.model.CallLogModel;
import com.arvoice.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ContactDetailsActivity extends AppCompatActivity implements CallDetailsAdapter.ItemClick{
    private ActivityContactDetailsBinding binding;
    private String number;
    private Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        Intent intent = getIntent();
        if (intent!=null){
            number = intent.getStringExtra("number");
        }
        initViews();
    }

    private void initViews() {
        binding.phoneNumber.setText(number);
        binding.ivBack.setOnClickListener(view -> finish());
        binding.llAddLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,AddLeadsActivity.class);
                intent.putExtra("number",number);
                startActivity(intent);
            }
        });
        binding.llSimOne.setOnClickListener(v -> makeCall(number.toString(), 0));
        binding.llSimTwo.setOnClickListener(v -> makeCall(number.toString(), 1));
        binding.llSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get phone number from TextView

                if (!number.isEmpty()) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setData(Uri.parse("sms:" + number));
                    smsIntent.putExtra("sms_body", ""); // Optional: pre-fill the SMS body
                    view.getContext().startActivity(smsIntent);
                } else {
                    Toast.makeText(view.getContext(), "Enter a phone number first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<CallLogModel> logs = getCallLogsByNumber(this, number);
        CallDetailsAdapter adapter = new CallDetailsAdapter(logs,this);
        binding.recyclerView.setAdapter(adapter);
    }
    public List<CallLogModel> getCallLogsByNumber(Context context, String targetNumber) {
        List<CallLogModel> list = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                CallLog.Calls.NUMBER + " = ?",
                new String[]{targetNumber},
                CallLog.Calls.DATE + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                int type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                long date = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));

                // SIM slot ID: Note, this is often vendor/device-specific and not guaranteed on all Android versions
                String simInfo = "";
                int simIndex = -1;
                int simColumnIndex = cursor.getColumnIndex("sim_id"); // Common on many devices
                if (simColumnIndex != -1) {
                    simIndex = cursor.getInt(simColumnIndex);
                    simInfo = simIndex == 0 ? "SIM 1" : simIndex == 1 ? "SIM 2" : "Unknown SIM";
                } else {
                    String phoneAccountId = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.PHONE_ACCOUNT_ID));
                    if (phoneAccountId != null) {
                        if (phoneAccountId.toLowerCase().contains("1")) {
                            simInfo = "SIM 1";
                        } else if (phoneAccountId.toLowerCase().contains("2")) {
                            simInfo = "SIM 2";
                        } else {
                            simInfo = "Unknown SIM";
                        }
                    }
                }

                String callTypeStr;
                switch (type) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        callTypeStr = "Outgoing";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        callTypeStr = "Answered";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        callTypeStr = "Not connected";
                        break;
                    default:
                        callTypeStr = "Other";
                }

                list.add(new CallLogModel(number, callTypeStr, duration, new Date(date), simInfo));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return list;
    }

    private void makeCall(String number, int simSlot) {
        if (number.isEmpty()) {
            Utils.showToast("Please enter a number");
            return;
        }

        TelecomManager telecomManager = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        SubscriptionManager subscriptionManager = (SubscriptionManager) mContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ContactDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ContactDetailsActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
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

    }
}
