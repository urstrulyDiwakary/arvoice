package com.arvoice.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arvoice.R;
import com.arvoice.activity.AddLeadsActivity;
import com.arvoice.activity.AddTasksActivity;
import com.arvoice.activity.EditTasksActivity;
import com.arvoice.activity.TransitionActivity;
import com.arvoice.adapter.TaskListAdapter;
import com.arvoice.databinding.SiteFragmentBinding;
import com.arvoice.model.TaskListModel;
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

public class SiteVisitingFragment extends Fragment implements TaskListAdapter.OnCardClickListener, TaskListAdapter.OnEditClickListener {
    private SiteFragmentBinding binding;
    ApiInterface apiInterface;
    private Context mContext;
    private List<TaskListModel.TaskItem> taskItemArrayList = new ArrayList<>();
    private TaskListAdapter taskListAdapter;

    private boolean isExpanded = false;
    String authToken;
    int categoryName;

    private boolean prevIsOpen = false, prevIsOverdue = false, prevIsClosed = false;
    private int prevDateSelectionIndex = 0;
    private List<String> prevSelectedUserIds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SiteFragmentBinding.inflate(getLayoutInflater());
        mContext = getActivity();
        apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        if (getArguments() != null) {
            categoryName = getArguments().getInt("templateID", -1);
        }
        getSavedToken();
        initView();
        listener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        callGetFilters();
    }

    private void listener() {
        binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        ActivityResultLauncher<Intent> updateLeadLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        callGetFilters();
                    }
                });

        binding.fabAddLead.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, AddTasksActivity.class);
            updateLeadLauncher.launch(intent);
        });

        binding.ivFilter.setOnClickListener(view -> showFilterDialog());

        binding.searchView.setOnSearchClickListener(v -> binding.tvHeading.setVisibility(View.GONE));
        binding.searchView.setOnCloseListener(() -> {
            binding.tvHeading.setVisibility(View.VISIBLE);
            return false;
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                taskListAdapter.filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                taskListAdapter.filterList(newText);
                return true;
            }
        });
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = getLayoutInflater().inflate(R.layout.filter_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        ImageView ivClose = dialogView.findViewById(R.id.ivClose);
        CheckBox cbOpen = dialogView.findViewById(R.id.checkbox_open);
        CheckBox cbOverdue = dialogView.findViewById(R.id.checkbox_overdue);
        CheckBox cbClosed = dialogView.findViewById(R.id.checkbox_closed);
        Spinner dateSpinner = dialogView.findViewById(R.id.date_spinner);
        CheckBox selectAllCheckbox = dialogView.findViewById(R.id.checkbox_select_all);
        ListView userListView = dialogView.findViewById(R.id.user_list);
        Button applyBtn = dialogView.findViewById(R.id.btn_apply_filter);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                mContext,
                android.R.layout.simple_spinner_item,
                new String[]{"All Days", "Today", "This Week", "This Month"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(spinnerAdapter);
        dateSpinner.setSelection(prevDateSelectionIndex);

        List<UserModel> userListData = new ArrayList<>();
        ArrayAdapter<UserModel> userAdapter = new ArrayAdapter<>(
                mContext,
                android.R.layout.simple_list_item_multiple_choice,
                userListData
        );
        userListView.setAdapter(userAdapter);
        userListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        cbOpen.setChecked(prevIsOpen);
        cbOverdue.setChecked(prevIsOverdue);
        cbClosed.setChecked(prevIsClosed);

        callUsers(userListData, userAdapter, userListView);

        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (int i = 0; i < userAdapter.getCount(); i++) {
                userListView.setItemChecked(i, isChecked);
            }
        });

        ivClose.setOnClickListener(view -> dialog.dismiss());
        applyBtn.setOnClickListener(v -> {
            prevIsOpen = cbOpen.isChecked();
            prevIsOverdue = cbOverdue.isChecked();
            prevIsClosed = cbClosed.isChecked();
            prevDateSelectionIndex = dateSpinner.getSelectedItemPosition();
            String selectedDate = dateSpinner.getSelectedItem().toString();

            prevSelectedUserIds.clear();
            for (int i = 0; i < userListView.getCount(); i++) {
                if (userListView.isItemChecked(i)) {
                    UserModel selectedUser = userAdapter.getItem(i);
                    if (selectedUser != null) {
                        prevSelectedUserIds.add(selectedUser.getUserid());
                    }
                }
            }
            boolean isAnyFilterApplied = prevIsOpen || prevIsOverdue || prevIsClosed ||
                    !prevSelectedUserIds.isEmpty() ||
                    !selectedDate.equalsIgnoreCase("All Days");
            if (isAnyFilterApplied) {
                binding.filterDot.setVisibility(View.VISIBLE);
            } else {
                binding.filterDot.setVisibility(View.GONE);
            }

            if (!prevIsOpen && !prevIsOverdue && !prevIsClosed &&
                    prevSelectedUserIds.isEmpty() &&
                    "All Days".equalsIgnoreCase(selectedDate)) {

                callGetFilters(); // ⬅️ Call original API
            } else {
                applyFilters(prevIsOpen, prevIsOverdue, prevIsClosed, selectedDate, prevSelectedUserIds);
            }
            dialog.dismiss();
        });
    }

    private void callUsers(List<UserModel> userListData, ArrayAdapter<UserModel> userAdapter, ListView userListView) {
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken;

        Call<List<UserModel>> call = apiInterface.getUsers(token);
        call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserModel>> call, @NonNull Response<List<UserModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userListData.clear();
                    userListData.addAll(response.body());
                    userAdapter.notifyDataSetChanged();

                    for (int i = 0; i < userAdapter.getCount(); i++) {
                        UserModel user = userAdapter.getItem(i);
                        if (user != null && prevSelectedUserIds.contains(user.getUserid())) {
                            userListView.setItemChecked(i, true);
                        }
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

    private void applyFilters(boolean isOpen, boolean isOverdue, boolean isClosed, String dateFilter, List<String> selectedUserIds) {
        List<TaskListModel.TaskItem> filteredList = new ArrayList<>();

        for (TaskListModel.TaskItem item : taskItemArrayList) {
            boolean statusMatch = (
                    (isOpen && "OPEN".equalsIgnoreCase(item.status)) ||
                            (isOverdue && "OVERDUE".equalsIgnoreCase(item.status)) ||
                            (isClosed && "CLOSED".equalsIgnoreCase(item.status))
            );
            boolean userMatch = selectedUserIds.isEmpty() || selectedUserIds.contains(item.assignedTo);
            boolean dateMatch = true;

            if (statusMatch && userMatch && dateMatch) {
                filteredList.add(item);
            }
        }
        taskListAdapter.updateFullData(filteredList);
    }

    private void getSavedToken() {
        SharedPreferences prefs = mContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = prefs.getString("token", null);
        Log.e("SiteVisi","***"+authToken);
    }

    private void callGetFilters() {
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken;

        Call<List<TaskListModel.TaskItem>> call = apiInterface.getTask(token);
        call.enqueue(new Callback<List<TaskListModel.TaskItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<TaskListModel.TaskItem>> call, @NonNull Response<List<TaskListModel.TaskItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    taskItemArrayList.clear();
                    taskItemArrayList.addAll(response.body());
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Log.e("Lead", "Lead Listing JSON:\n" + gson.toJson(taskItemArrayList));
                    taskListAdapter.updateFullData(taskItemArrayList);
                    binding.tvNoData.setVisibility(taskItemArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.recyclerView.setVisibility(taskItemArrayList.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    binding.tvNoData.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TaskListModel.TaskItem>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                Utils.showToast("Error: " + t.getMessage());
            }
        });
    }

    private void initView() {
        taskListAdapter = new TaskListAdapter(mContext, taskItemArrayList, this, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        binding.recyclerView.setAdapter(taskListAdapter);

        binding.fabAddLead.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, AddLeadsActivity.class);
            intent.putExtra("templateID", categoryName);
            updateLeadLauncher.launch(intent);
        });
    }

    ActivityResultLauncher<Intent> updateLeadLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    categoryName = result.getData().getIntExtra("templateID", -1);
                    callGetFilters();
                }
            });

    ActivityResultLauncher<Intent> editLeadLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    callGetFilters();
                }
            });

    @Override
    public void onCardClick(int position) {
        binding.searchView.setQuery("", false);
        binding.searchView.clearFocus();
        binding.searchView.setIconified(true);
        binding.tvHeading.setVisibility(View.VISIBLE);

        TaskListModel.TaskItem taskItem = taskItemArrayList.get(position);
        Intent intent = new Intent(mContext, TransitionActivity.class);
        intent.putExtra("taskID", taskItem.taskId);
        updateLeadLauncher.launch(intent);
    }

    @Override
    public void OnEditClickListener(int position) {
        TaskListModel.TaskItem taskItem = taskItemArrayList.get(position);
        Gson gson = new Gson();
        String taskJson = gson.toJson(taskItem);
        Intent intent = new Intent(mContext, EditTasksActivity.class);
        intent.putExtra("taskJson", taskJson);
        editLeadLauncher.launch(intent);
    }
}