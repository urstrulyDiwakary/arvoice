package com.arvoice.activity;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arvoice.R;
import com.arvoice.databinding.ActivityAddLeadBinding;
import com.arvoice.databinding.ActivityAddTaskBinding;
import com.arvoice.model.LeadsListModel;
import com.arvoice.model.UserModel;
import com.arvoice.networkinh.ApiClient;
import com.arvoice.networkinh.ApiInterface;
import com.arvoice.utils.Constant;
import com.arvoice.utils.ProgressUtils;
import com.arvoice.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddTasksActivity extends AppCompatActivity{
    private Context mContext;
    private ActivityAddTaskBinding binding;
    String authToken;
    String userName;
    private ActivityResultLauncher<Intent> userResultLauncher;
    private ActivityResultLauncher<Intent> leadResultLauncher;
    String leadJson;
    String assignedId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;

        getSavedToken();
        listener();
        userResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        UserModel selectedUser = (UserModel) result.getData().getSerializableExtra("selectedUser");
                        if (selectedUser != null) {
                            binding.etAssignedTo.setText(selectedUser.getUsername());
                            binding.etAssignedTo.setError(null); // <-- clear error
                            assignedId = selectedUser.getUserid();
                        }
                    }
                });
        leadResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        LeadsListModel.ContentItem selectedUser = result.getData().getParcelableExtra("selectedLead");

                        if (selectedUser != null) {
                            Gson gson = new Gson();
                            leadJson = gson.toJson(selectedUser);
                            Log.d("SelectedUserJSON", leadJson); // or print to console
                            binding.etRelatedLead.setText(selectedUser.getLeadOwner());
                            binding.etRelatedLead.setError(null); // <-- clear error

                        }
                    }
                });
    }

    private void getSavedToken() {
        SharedPreferences prefs = mContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = prefs.getString("token", null); // second parameter is default value if not found
        userName = prefs.getString("userName", null); // second parameter is default value if not found
        Log.e("Add Lead","****"+userName);
    }


    private void listener() {
        binding.etStatus.setText("OPEN");
        binding.topbar.setOnClickListener(view -> finish());
        binding.etAssignedTo.setOnClickListener(view -> {
            Intent intent = new Intent(this, SelectUserActivity.class);
            userResultLauncher.launch(intent);
        });
        binding.etRelatedLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SelectLeadActivity.class);
                intent.putExtra("templateID",66);
                leadResultLauncher.launch(intent);
            }
        });
        binding.etDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        binding.etDueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });
        binding.etStatus.setOnClickListener(view -> showStatusBottomSheet());
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.etName.getText().toString().trim();
                String assigned = binding.etAssignedTo.getText().toString().trim();
                String status = binding.etStatus.getText().toString().trim();
                String dateStr = binding.etDueDate.getText().toString().trim();  // e.g., 2025-05-02
                String timeStr = binding.etDueTime.getText().toString().trim();  // e.g., 14:30
                String relatedLead = binding.etRelatedLead.getText().toString().trim();  // e.g., 14:30

                if (name.isEmpty()) {
                    binding.etName.setError("Please enter Site Visit Details");
                    binding.etName.requestFocus();
                    return;
                }
                if (assigned.isEmpty()) {
                    binding.etAssignedTo.setError("Please select Assigned To");
                    binding.etAssignedTo.requestFocus();
                    return;
                }if (status.isEmpty()) {
                    binding.etStatus.setError("Please select Status");
                    binding.etStatus.requestFocus();
                    return;
                }

                if (dateStr.isEmpty()) {
                    binding.etDueDate.setError("Please select a Due Date");
                    binding.etDueDate.requestFocus();
                    return;
                }

                if (timeStr.isEmpty()) {
                    binding.etDueTime.setError("Please select a Due Time");
                    binding.etDueTime.requestFocus();
                    return;
                }
                if (relatedLead.isEmpty()) {
                    binding.etRelatedLead.setError("Please select Related Lead");
                    binding.etRelatedLead.requestFocus();
                    return;
                }
                try {
                    String input = dateStr + " " + timeStr;  // e.g., "2025-05-02 14:30"
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    Date date = inputFormat.parse(input);

                    // Format to ISO-like string
                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    String dueDate = isoFormat.format(date);

                    JSONObject leadObject = new JSONObject(leadJson);  // Parse string to JSONObject

                    JSONObject json = new JSONObject();
                    json.put("taskName", binding.etName.getText().toString());
                    json.put("assignedTo", binding.etAssignedTo.getText().toString());
                    json.put("dueDate", dueDate);
                    json.put("lead", leadObject);
                    json.put("status", binding.etStatus.getText().toString());
                    json.put("comments", binding.etComments.getText().toString());
                    json.put("description", "");

                    // Proceed with further logic (e.g., API call)
                    Log.d("SaveData", json.toString());
                    callAddTask(json.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
    private void callAddTask(String criteriaJson) {
        ProgressUtils.showProgressDialog(AddTasksActivity.this);
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your actual method to get token

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                criteriaJson
        );
        Call<String> call = apiInterface.addTask(token,requestBody);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                ProgressUtils.hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(mContext,"Added Successfully",Toast.LENGTH_SHORT).show();

                    Log.d("RESPONSE", "Success! Code: " + response.code());
                    Log.d("BODY", "Message: " + response.body());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("done", '0');
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

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // 0 = January
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                mContext,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Create Calendar object with selected date
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                    // Format to yyyy-MM-dd
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = sdf.format(selectedCalendar.getTime());

                    binding.etDueDate.setText(formattedDate);
                    binding.etDueDate.setError(null); // <-- clear error

                },
                year, month, day
        );

        datePickerDialog.show();
    }
    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                mContext,
                (view, selectedHour, selectedMinute) -> {
                    // Format time as HH:mm (24-hour)
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    binding.etDueTime.setText(formattedTime);
                    binding.etDueTime.setError(null); // <-- clear error

                },
                hour,
                minute,
                true // <-- true for 24-hour format
        );

        timePickerDialog.show();
    }

    private void showStatusBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_status, null);
        bottomSheetDialog.setContentView(sheetView);

        ImageView ivClose = sheetView.findViewById(R.id.ivClose);
        TextView status_open = sheetView.findViewById(R.id.status_open);
        TextView overdue = sheetView.findViewById(R.id.status_overdue);
        TextView closed = sheetView.findViewById(R.id.status_closed);
        TextView approved = sheetView.findViewById(R.id.status_approved);
        TextView rejected = sheetView.findViewById(R.id.status_rejected);
        TextView transferred = sheetView.findViewById(R.id.status_transferred);

        ivClose.setOnClickListener(view -> bottomSheetDialog.dismiss());
        View.OnClickListener listener = v -> {
            String selectedStatus = ((TextView) v).getText().toString();
            binding.etStatus.setText(selectedStatus);
            binding.etStatus.setError(null); // <-- clear error
            bottomSheetDialog.dismiss();
            // Handle your status selection here
        };

        overdue.setOnClickListener(listener);
        closed.setOnClickListener(listener);
        approved.setOnClickListener(listener);
        rejected.setOnClickListener(listener);
        transferred.setOnClickListener(listener);
        status_open.setOnClickListener(listener);

        bottomSheetDialog.show();
    }




}

