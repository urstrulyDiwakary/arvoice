package com.arvoice.activity;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arvoice.R;
import com.arvoice.adapter.TransitionAdapter;
import com.arvoice.databinding.ActivityAddLeadBinding;
import com.arvoice.databinding.ActivityTransitionBinding;
import com.arvoice.model.TransitionModel;
import com.arvoice.networkinh.ApiClient;
import com.arvoice.networkinh.ApiInterface;
import com.arvoice.utils.Constant;
import com.arvoice.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TransitionActivity extends AppCompatActivity implements TransitionAdapter.OnCardClickListener{
    private Context mContext;
    private ActivityTransitionBinding binding;
    String authToken;
    int taskID;
    private TransitionAdapter adapter;
    private List<TransitionModel.TaskItem> taskList = new ArrayList<>();
    private static final int REQUEST_WRITE_EXTERNAL = 101;
    private Bitmap pendingBitmapToSave = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransitionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        listener();
        Intent intent = getIntent();
        if (intent!=null){
            taskID = intent.getIntExtra("taskID",0);
            fetTransitionData();
        }
    }



    private void fetTransitionData() {
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your actual method to get token

        Call<List<TransitionModel.TaskItem>> call = apiInterface.getTransition(taskID,token);
        call.enqueue(new Callback<List<TransitionModel.TaskItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<TransitionModel.TaskItem>> call, @NonNull Response<List<TransitionModel.TaskItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    taskList.clear();
                    taskList.addAll(response.body());
                    adapter.updateList(taskList);

                    if (taskList.isEmpty()) {
                        binding.tvNoData.setVisibility(View.VISIBLE);
                        binding.recyclerView.setVisibility(View.GONE);
                    } else {
                        binding.tvNoData.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.tvNoData.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                    Utils.showToast(Constant.API_CODES.ON_FAILURE_MESSAGE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TransitionModel.TaskItem>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                Utils.showToast("Error: " + t.getMessage());
            }
        });


    }


    private void listener() {
        binding.topbar.setOnClickListener(view -> finish());
        adapter = new TransitionAdapter(mContext,taskList,this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }


    @Override
    public void onCardClick(String image) {
        if (image != null && !image.trim().isEmpty()) {
            if (image.contains(",")) {
                image = image.split(",")[1]; // remove base64 prefix if exists
            }

            try {
                byte[] decodedBytes = Base64.decode(image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                checkPermissionAndSave(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Invalid image data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No image to download", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkPermissionAndSave(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ does NOT require storage permission
            saveImageToGallery(this, bitmap);
        } else {
            // Android 9 and below requires permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                saveImageToGallery(this, bitmap);
            } else {
                // Store temporarily until permission is granted
                pendingBitmapToSave = bitmap;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingBitmapToSave != null) {
                    saveImageToGallery(this, pendingBitmapToSave);
                    pendingBitmapToSave = null;
                }
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void saveImageToGallery(Context context, Bitmap bitmap) {
        String filename = "image_" + System.currentTimeMillis() + ".png";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyAppImages");
        } else {
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyAppImages");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, filename);
            try (OutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show();

                // Scan file to show in gallery immediately
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(Uri.fromFile(file));
                sendBroadcast(scanIntent);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // For Android 10+ via MediaStore
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            if (uri != null) {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                if (outputStream != null) outputStream.close();
                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }



}

