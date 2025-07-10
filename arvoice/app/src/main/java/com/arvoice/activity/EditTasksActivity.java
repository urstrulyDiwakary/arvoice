package com.arvoice.activity;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arvoice.R;
import com.arvoice.adapter.CarListAdapter;
import com.arvoice.databinding.ActivityAddTaskBinding;
import com.arvoice.databinding.ActivityEditTaskBinding;
import com.arvoice.model.CarListModel;
import com.arvoice.model.LeadsListModel;
import com.arvoice.model.TaskListModel;
import com.arvoice.model.UserModel;
import com.arvoice.networkinh.ApiClient;
import com.arvoice.networkinh.ApiInterface;
import com.arvoice.utils.Constant;
import com.arvoice.utils.ProgressUtils;
import com.arvoice.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditTasksActivity extends AppCompatActivity{
    private Context mContext;
    private ActivityEditTaskBinding binding;
    String authToken;
    String userName;
    private ActivityResultLauncher<Intent> userResultLauncher;
    private ActivityResultLauncher<Intent> leadResultLauncher;
    String leadJson;
    String assignedId;
    String taskJson;
    int  taskId;
    private static final int REQUEST_GALLERY = 100;
    private String base64Image;
    private List<CarListModel.CarItem> carItemArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        Intent intent = getIntent();
        if (intent!=null){
            taskJson = intent.getStringExtra("taskJson");
            Log.i("Task",taskJson.toString());
            setData(taskJson);
        }

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
        callCardFilters();
    }
    private void callCardFilters() {
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken;

        Call<List<CarListModel.CarItem>> call = apiInterface.getCarList(token);
        call.enqueue(new Callback<List<CarListModel.CarItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<CarListModel.CarItem>> call, @NonNull Response<List<CarListModel.CarItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    carItemArrayList.clear();
                    carItemArrayList.addAll(response.body());
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Log.e("Lead", "Lead Listing JSON:\n" + gson.toJson(carItemArrayList));

                } else {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Log.e("Lead", "Lead Listing JSON:\n" + gson.toJson(carItemArrayList));

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CarListModel.CarItem>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                Utils.showToast("Error: " + t.getMessage());
            }
        });
    }

    private void showCarListBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_car_list, null);
        bottomSheetDialog.setContentView(sheetView);

        ImageView ivClose = sheetView.findViewById(R.id.ivClose);
        RecyclerView recyclerView = sheetView.findViewById(R.id.recyclerCarList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CarListAdapter adapter = new CarListAdapter(carItemArrayList, selectedItem -> {
            // Handle selection
            binding.eTCar.setText(selectedItem.getCarName());
            bottomSheetDialog.dismiss();
        });

        recyclerView.setAdapter(adapter);

        ivClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void setData(String taskJson){
        try {
            JSONObject taskObject = new JSONObject(taskJson);

            taskId = taskObject.getInt("taskId");
            String name = taskObject.getString("taskName");
            String assignedTo = taskObject.getString("assignedTo");
            String status = taskObject.getString("status");
            String comments = taskObject.getString("comments");
            String dueDate = taskObject.getString("dueDate");

            if (dueDate.contains("T")) {
                String[] parts = dueDate.split("T");
                String datePart = parts[0]; // "2025-06-13"
                String timePart = parts[1]; // "00:01:00"

                // Optional: trim seconds if not needed
                timePart = timePart.substring(0, 5); // "00:01"

                binding.etDueDate.setText(datePart);
                binding.etDueTime.setText(timePart);
                Log.d("Date", datePart);
                Log.d("Time", timePart);
            }
            leadJson = taskObject.getString("lead");
            JSONObject leadObject = new JSONObject(leadJson);
            String leadOwner = leadObject.getString("leadOwner");

            binding.etRelatedLead.setText(leadOwner);
            binding.etName.setText(name);
            binding.etAssignedTo.setText(assignedTo);
            binding.etStatus.setText(status);
            binding.etComments.setText(comments);

            String car = taskObject.getString("car");
            if(status.equalsIgnoreCase("CLOSED")||status.equalsIgnoreCase("APPROVED")){
                binding.llCar.setVisibility(View.VISIBLE);
                binding.eTCar.setText(car);
            }
            Log.d("TaskName", taskObject.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void getSavedToken() {
        SharedPreferences prefs = mContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = prefs.getString("token", null); // second parameter is default value if not found
        userName = prefs.getString("userName", null); // second parameter is default value if not found
        Log.e("Add Lead","****"+userName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    binding.imagePreview.setImageBitmap(bitmap);

                    // Convert to Base64
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte[] byteArray = stream.toByteArray();
                    base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    // 🔹 You can now use base64Image in your API payload
                    Log.d("BASE64_IMAGE", base64Image);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private void checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 101);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            } else {
                openGallery();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(this, "Permission denied to access images", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void listener() {
        binding.eTCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCarListBottomSheet();
            }
        });
        binding.chooseImageButton.setOnClickListener(v -> {
            checkPermissionAndOpenGallery();
        });
        binding.imagePreview.setOnClickListener(v -> {
            checkPermissionAndOpenGallery();
        });
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
                String dateStr = binding.etDueDate.getText().toString().trim();
                String timeStr = binding.etDueTime.getText().toString().trim();
                String relatedLead = binding.etRelatedLead.getText().toString().trim();

// ✅ Validate name
                if (name.isEmpty()) {
                    binding.etName.setError("Please enter Site Visit Details");
                    binding.etName.requestFocus();
                    return;
                }

// ✅ Validate assigned
                if (assigned.isEmpty()) {
                    binding.etAssignedTo.setError("Please select Assigned To");
                    binding.etAssignedTo.requestFocus();
                    return;
                }

// ✅ Validate status
                if (status.isEmpty()) {
                    binding.etStatus.setError("Please select Status");
                    binding.etStatus.requestFocus();
                    return;
                }

// ✅ If status is CLOSED, check image

                if (status.equalsIgnoreCase("CLOSED") || status.equalsIgnoreCase("APPROVED")) {
                    if (binding.eTCar.getText().toString().trim().isEmpty()) {
                        binding.eTCar.setError("Please select a car");
                        binding.eTCar.requestFocus();
                        return;
                    }
                }

                if (status.equalsIgnoreCase("CLOSED")) {
                    if (base64Image == null || base64Image.isEmpty()) {
                        Toast.makeText(mContext, "Image is required when status is CLOSED", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (status.equalsIgnoreCase("APPROVED") || status.equalsIgnoreCase("REJECTED") || status.equalsIgnoreCase("COMPLETED") || status.equalsIgnoreCase("CLOSED")) {
                    if (binding.etComments.getText().toString().trim().isEmpty()) {
                        binding.etComments.setError("Please add Comments");
                        binding.etComments.requestFocus();
                        return;
                    }
                }
// ✅ Validate date
                if (dateStr.isEmpty()) {
                    binding.etDueDate.setError("Please select a Due Date");
                    binding.etDueDate.requestFocus();
                    return;
                }

// ✅ Validate time
                if (timeStr.isEmpty()) {
                    binding.etDueTime.setError("Please select a Due Time");
                    binding.etDueTime.requestFocus();
                    return;
                }

// ✅ Validate related lead
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
                    json.put("taskId", taskId);
                    json.put("taskName", binding.etName.getText().toString());
                    json.put("assignedTo", binding.etAssignedTo.getText().toString());
                    json.put("dueDate", dueDate);
                    json.put("lead", leadObject);
                    json.put("car", binding.eTCar.getText().toString());
                    json.put("status", binding.etStatus.getText().toString());
                    json.put("comments", binding.etComments.getText().toString());
                    json.put("description", "");
                    if (base64Image != null && !base64Image.isEmpty()) {
                        json.put("siteVisitImage", base64Image);
                    } else {
                        json.put("siteVisitImage", ""); // Empty array if no image
                    }
                    Log.i("EditTeak",json.toString());
                    // Proceed with further logic (e.g., API call)
                    saveJsonToInternal(mContext, "response_data.txt", json.toString());
                    callUpdateTask(json.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
    private void saveJsonToInternal(Context context, String fileName, String jsonString) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();
            Log.d("SaveData", "Saved internally at: " + context.getFilesDir() + "/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SaveData", "Failed to save JSON internally", e);
        }
    }


    private void printFullLog(String tag, String message) {
        int maxLogSize = 1000;
        for (int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = Math.min((i + 1) * maxLogSize, message.length());
            Log.d(tag, message.substring(start, end));
        }
    }

    private void printPureJson(String json) {
        int maxChunkSize = 1000;
        for (int i = 0; i <= json.length() / maxChunkSize; i++) {
            int start = i * maxChunkSize;
            int end = Math.min((i + 1) * maxChunkSize, json.length());
            System.out.print(json.substring(start, end));
        }
    }


    private void callUpdateTask(String criteriaJson) {
        printPureJson(criteriaJson);

        ProgressUtils.showProgressDialog(EditTasksActivity.this);
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your actual method to get token
        Log.e("Edit","Token****"+token);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                criteriaJson
        );
        Call<String> call = apiInterface.updateTask(token,taskId,requestBody);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                ProgressUtils.hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(mContext,"Updated Successfully",Toast.LENGTH_SHORT).show();

                    Log.d("RESPONSE", "Success! Code: " + response.code());
                    Log.d("BODY", "Message: " + response.body());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("done", '0');
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Log.d("RESPONSE", "Success! Code: " + response.code());
                    Log.d("BODY", "Message: " + response.body());
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
            if (selectedStatus.equalsIgnoreCase("CLOSED")) {
                binding.llUploadImage.setVisibility(View.VISIBLE);
            } else {
                binding.llUploadImage.setVisibility(View.GONE);
            }

            if (selectedStatus.equalsIgnoreCase("CLOSED")||selectedStatus.equalsIgnoreCase("APPROVED")) {
                binding.llCar.setVisibility(View.VISIBLE);
            } else {
                binding.llCar.setVisibility(View.GONE);
                binding.eTCar.setText("");
            }

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

