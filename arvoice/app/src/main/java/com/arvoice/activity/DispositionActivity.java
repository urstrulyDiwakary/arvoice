package com.arvoice.activity;



import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.arvoice.R;
import com.arvoice.databinding.ActivityDispositionBinding;
import com.arvoice.model.Field;
import com.arvoice.model.Group;
import com.arvoice.model.LeadCreate;
import com.arvoice.networkinh.ApiClient;
import com.arvoice.networkinh.ApiInterface;
import com.arvoice.utils.Constant;
import com.arvoice.utils.ProgressUtils;
import com.arvoice.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DispositionActivity extends AppCompatActivity{
    private Context mContext;
    private ActivityDispositionBinding binding;
    String authToken;
    String userName;
    int leadID;
    String requestJson;
    List<String> leadStageOptions = new ArrayList<>();
    JSONObject leadInfoExtnAttr = new JSONObject();
    JSONObject jsonObject;
    int categoryName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDispositionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        Intent intent = getIntent();
        if (intent!=null){
            leadID = intent.getIntExtra("leadID",0);
            requestJson = intent.getStringExtra("Json");
            categoryName = intent.getIntExtra("templateID",0);
            setJsonDate();
        }

        getSavedToken();
        listener();
    }

    private void setJsonDate() {
        try {
            Log.e("Disposition","JSON****"+requestJson);
            jsonObject = new JSONObject(requestJson);

            String contactName = jsonObject.getString("contactName");
            String leadOwner = jsonObject.getString("leadOwner");
            jsonObject.put("contactName",contactName);
            jsonObject.put("leadOwner",leadOwner);

            binding.etLeadState.setText(jsonObject.getString("leadStage"));
            binding.inputRevenue.setText(jsonObject.getString("expectedRevenue"));
            binding.inputClosingDate.setText(jsonObject.getString("expectedClosingDate"));

            leadInfoExtnAttr = jsonObject.getJSONObject("leadInfoExtnAttr");
            binding.inputFollowupDate.setText(leadInfoExtnAttr.getString("nextFollow-upOn"));
            binding.inputNotes.setText(leadInfoExtnAttr.getString("nextFollow-upNotes"));
            String selectedReason = leadInfoExtnAttr.getString("description"); // ← this is your selected value
            for (int i = 0; i < binding.radioGroupReasons.getChildCount(); i++) {
                View view = binding.radioGroupReasons.getChildAt(i);
                if (view instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) view;
                    if (radioButton.getText().toString().equalsIgnoreCase(selectedReason)) {
                        radioButton.setChecked(true);
                        break;
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void getSavedToken() {
        SharedPreferences prefs = mContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = prefs.getString("token", null); // second parameter is default value if not found
        userName = prefs.getString("userName", null); // second parameter is default value if not found
        Log.e("Add Lead","****"+userName);
        callGetForm();
    }

    private void callGetForm() {
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your actual method to get token

        Call<LeadCreate> call = apiInterface.getLeadForm(token);
        call.enqueue(new Callback<LeadCreate>() {
            @Override
            public void onResponse(@NonNull Call<LeadCreate> call, @NonNull Response<LeadCreate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    leadStageOptions.clear();
                    LeadCreate leadCreate = response.body();

                    for (Group group : leadCreate.getGroups()) {
                        List<Field> fields = group.getFields();
                        if (fields != null) {
                            for (Field field : fields) {
                                if ("leadStage".equalsIgnoreCase(field.getName())) {
                                    String optionsStr = field.getOptions(); // Example: "NEW | CONTACTED | ..."
                                    if (optionsStr != null && !optionsStr.isEmpty()) {
                                        String[] optionsArray = optionsStr.split("\\|");
                                        for (String opt : optionsArray) {
                                            opt = opt.trim();
                                            if (!opt.isEmpty()) {
                                                leadStageOptions.add(opt);
                                            }
                                        }
                                    }
                                    break; // found the field, no need to continue
                                }
                            }
                        }
                        break; // found the group, no need to continue
                    }

                } else {
                    Utils.showToast(Constant.API_CODES.ON_FAILURE_MESSAGE);
                    Log.e("API_ERROR", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LeadCreate> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                Utils.showToast("Error: " + t.getMessage());
            }
        });

    }

    private void listener() {
        binding.inputClosingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // Zero-padded month and day
                            String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                            binding.inputClosingDate.setText(selectedDate);
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
        binding.inputFollowupDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                    (view, year, month, dayOfMonth) -> {
                        final int selectedYear = year;
                        final int selectedMonth = month;
                        final int selectedDay = dayOfMonth;

                        // After date is picked, open time picker
                        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,
                                (timeView, hourOfDay, minute) -> {
                                    Calendar selectedDateTime = Calendar.getInstance();
                                    selectedDateTime.set(selectedYear, selectedMonth, selectedDay, hourOfDay, minute);

                                    SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.getDefault());
                                    String formattedDisplay = displayFormat.format(selectedDateTime.getTime());
                                    binding.inputFollowupDate.setText(formattedDisplay.replace("\\/", "/"));

                                    SimpleDateFormat sendFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                                    String followUpDate = sendFormat.format(selectedDateTime.getTime());
                                    try {
                                        leadInfoExtnAttr.put("nextFollow-upOn", followUpDate);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                false
                        );

                        timePickerDialog.show();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();
        });
        binding.etLeadState.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
            View sheetView = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_layout, null);

            ListView listView = sheetView.findViewById(R.id.listViewOptions);
            ArrayAdapter<String> bottomSheetAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, leadStageOptions);
            listView.setAdapter(bottomSheetAdapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                binding.etLeadState.setText(leadStageOptions.get(position));
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();
        });
        binding.ivBack.setOnClickListener(view -> finish());
        binding.btnNotConnected.setOnClickListener(v -> {
            binding.cvConnected.setVisibility(View.GONE);
            binding.cvReason.setVisibility(View.VISIBLE);
            binding.btnConnected.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            binding.btnNotConnected.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
            binding.btnConnected.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
        });
        binding.btnConnected.setOnClickListener(v -> {
            finish();
//            binding.cvConnected.setVisibility(View.VISIBLE);
//            binding.cvReason.setVisibility(View.GONE);
//            binding.btnConnected.setTextColor(ContextCompat.getColor(mContext, R.color.white));
//            binding.btnConnected.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
//            binding.btnNotConnected.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
        });
        binding.btnSubmit.setOnClickListener(view -> {
            // You can read values here
            // Example:
            int selectedId = binding.radioGroupReasons.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadio = findViewById(selectedId);
                String reason = selectedRadio.getText().toString();
                try {
                    leadInfoExtnAttr.put("description", reason);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                leadInfoExtnAttr.put("nextFollow-upNotes",binding.inputNotes.getText().toString());
                jsonObject.put("leadStage",binding.etLeadState.getText().toString());
                jsonObject.put("expectedRevenue",binding.inputRevenue.getText().toString());
                jsonObject.put("expectedClosingDate",binding.inputClosingDate.getText().toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
            Log.e("Disposition","Json***"+jsonObject.toString());
            callUpdateLeads();

        });

    }
    private void callUpdateLeads() {
        ProgressUtils.showProgressDialog(DispositionActivity.this);

        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Get your actual token here

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                jsonObject.toString()
        );

        Call<String> call = apiInterface.updateLeads(token, leadID, requestBody);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                ProgressUtils.hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(mContext, "Lead Updated Successfully", Toast.LENGTH_SHORT).show();
                    Log.d("RESPONSE", "Success! Code: " + response.code());
                    Log.d("BODY", "Message: " + response.body());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("templateID", categoryName);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Utils.showToast(Constant.API_CODES.ON_FAILURE_MESSAGE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                ProgressUtils.hideProgressDialog();
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                Utils.showToast("Error: " + t.getMessage());
            }
        });
    }


}

