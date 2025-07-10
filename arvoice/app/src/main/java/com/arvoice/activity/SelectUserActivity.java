package com.arvoice.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arvoice.adapter.UserListAdapter;
import com.arvoice.databinding.ActivityAddTaskBinding;
import com.arvoice.databinding.ActivitySelectUserBinding;
import com.arvoice.model.UserModel;
import com.arvoice.networkinh.ApiClient;
import com.arvoice.networkinh.ApiInterface;
import com.arvoice.utils.Constant;
import com.arvoice.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SelectUserActivity extends AppCompatActivity implements UserListAdapter.OnCardClickListener{
    private Context mContext;
    private ActivitySelectUserBinding binding;
    String authToken;
    String userName;
    private List<UserModel> userListData = new ArrayList<>();

    private UserListAdapter userListAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;

        getSavedToken();
        initView();
        listener();
    }
    @Override
    public void onResume() {
        super.onResume();
        callUsers();
    }

    private void initView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        binding.recyclerView.setAdapter(userListAdapter);

        userListAdapter = new UserListAdapter(mContext,userListData,this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        binding.recyclerView.setAdapter(userListAdapter);



    }
    private void getSavedToken() {
        SharedPreferences prefs = mContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = prefs.getString("token", null); // second parameter is default value if not found
        userName = prefs.getString("userName", null); // second parameter is default value if not found
        Log.e("Add Lead","****"+userName);
    }

    private void callUsers() {
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your token

        Call<List<UserModel>> call = apiInterface.getUsers(token);
        call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserModel>> call, @NonNull Response<List<UserModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        userListData.clear();
                        userListData.addAll(response.body());

                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        Log.e("Lead", "Lead Listing JSON:\n" + gson.toJson(userListData));
                        userListAdapter.updateFullData(userListData);

                        if (userListData.isEmpty()) {
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
                } else {
                    Toast.makeText(mContext, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserModel>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "User fetch failed: " + t.getMessage());
                Toast.makeText(mContext, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listener() {
        binding.topbar.setOnClickListener(view -> finish());

        binding.searchView.setOnSearchClickListener(v -> binding.tvHeading.setVisibility(View.GONE));
        binding.searchView.setOnCloseListener(() -> {
            binding.tvHeading.setVisibility(View.VISIBLE);
            return false;
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                userListAdapter.filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userListAdapter.filterList(newText);
                return true;
            }
        });
    }


    @Override
    public void onCardClick(int position) {
        UserModel selectedUser = userListAdapter.getFilteredItem(position);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedUser", selectedUser);
        setResult(RESULT_OK, resultIntent);
        finish(); // Return

    }
}

