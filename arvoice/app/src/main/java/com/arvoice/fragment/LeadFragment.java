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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arvoice.R;
import com.arvoice.activity.AddLeadsActivity;
import com.arvoice.activity.UpdateLeadsActivity;
import com.arvoice.adapter.LeadListAdapter;
import com.arvoice.adapter.LeadOptionAdapter;
import com.arvoice.databinding.LeadFragmentBinding;
import com.arvoice.model.LeadsFilter;
import com.arvoice.model.LeadsListModel;
import com.arvoice.networkinh.ApiClient;
import com.arvoice.networkinh.ApiInterface;
import com.arvoice.utils.Constant;
import com.arvoice.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeadFragment extends Fragment implements LeadListAdapter.OnCardClickListener{
    private LeadFragmentBinding binding;
    ApiInterface apiInterface;
    private Context mContext;
    private List<LeadsFilter.TemplateItem> templateItemList = new ArrayList<>(); // Store full API response
    private LeadListAdapter leadListAdapter;
    private LeadOptionAdapter leadOptionAdapter;
    private List<LeadsListModel.ContentItem> leadListModelArrayList  = new ArrayList<>();

    private boolean isExpanded = false;
    String authToken;
    int categoryName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = LeadFragmentBinding.inflate(getLayoutInflater());
        mContext = getActivity();
        apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        if (getArguments() != null) {
            categoryName = getArguments().getInt("templateID", -1);
            Log.e("Lead","category"+categoryName);
            // Use the value as needed
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
                leadListAdapter.filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                leadListAdapter.filterList(newText);
                return true;
            }
        });

    }
    private void getSavedToken() {
        SharedPreferences prefs = mContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = prefs.getString("token", null); // second parameter is default value if not found
    }
    private void callGetFilters() {
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your actual method to get token
        Log.e("LEad","*****"+token);

        Call<LeadsFilter> call = apiInterface.getTemplate("leads",token);
        call.enqueue(new Callback<LeadsFilter>() {
            @Override
            public void onResponse(@NonNull Call<LeadsFilter> call, @NonNull Response<LeadsFilter> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LeadsFilter.TemplateItem> templateItemList = response.body().getTemplates();
                    // Print full JSON
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Log.e("Lead", "Response JSON:\n" + gson.toJson(templateItemList));
                    if (templateItemList != null && !templateItemList.isEmpty()) {
                        for (LeadsFilter.TemplateItem item : templateItemList) {
                            if (categoryName == item.getTemplateId()) {
                                binding.tvTitle.setText(item.getName());

                                int defaultIndex = 0;
                                for (int i = 0; i < templateItemList.size(); i++) {
                                    if (categoryName == templateItemList.get(i).getTemplateId()) {
                                        defaultIndex = i;
                                        break;
                                    }
                                }

                                leadOptionAdapter.updateFullData(templateItemList);
                                leadOptionAdapter.setSelectedIndex(defaultIndex);

                                LeadsFilter.TemplateItem defaultItem = templateItemList.get(defaultIndex);
                                binding.tvTitle.setText(defaultItem.getName());
                                callGetLeads(defaultItem.getSearchCriteriaList());
                                break;
                            }
                        }
                    }


                } else {
                    Utils.showToast(Constant.API_CODES.ON_FAILURE_MESSAGE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LeadsFilter> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                Utils.showToast("Error: " + t.getMessage());
            }
        });

    }

    private void callGetLeads(String criteriaJson) {
        Log.e("LLead","111"+criteriaJson);
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your actual method to get token

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                criteriaJson
        );
        Call<LeadsListModel> call = apiInterface.getLeads(token,"0","50","leadId","ASC", requestBody);
        call.enqueue(new Callback<LeadsListModel>() {
            @Override
            public void onResponse(@NonNull Call<LeadsListModel> call, @NonNull Response<LeadsListModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    leadListModelArrayList.clear();
                    leadListModelArrayList = response.body().getContent();
                    // Print full JSON
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Log.e("Lead", "Lead Listing JSON:\n" + gson.toJson(leadListModelArrayList));
                    leadListAdapter.updateFullData(leadListModelArrayList);
                } else {
                    Utils.showToast(Constant.API_CODES.ON_FAILURE_MESSAGE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LeadsListModel> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                Utils.showToast("Error: " + t.getMessage());
            }
        });

    }
    private void initView() {
        leadOptionAdapter = new LeadOptionAdapter(templateItemList, (item, criteria) -> {
            Log.e("LeadFrag", "Criteria***"+criteria);
            binding.tvTitle.setText(item);
            binding.recyclerOptions.setVisibility(View.GONE);
            binding.ivArrow.setRotation(0);
            isExpanded = false;
            callGetLeads(criteria);
        });


        binding.recyclerOptions.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        binding.recyclerOptions.setLayoutManager(new LinearLayoutManager(mContext));
        binding.recyclerOptions.setAdapter(leadOptionAdapter);

        binding.dropdownHeader.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            binding.recyclerOptions.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            binding.ivArrow.setRotation(isExpanded ? 180 : 0);
        });

        leadListAdapter = new LeadListAdapter(mContext,leadListModelArrayList,this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        binding.recyclerView.setAdapter(leadListAdapter);

        binding.fabAddLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddLeadsActivity.class);
                intent.putExtra("templateID", categoryName);
                updateLeadLauncher.launch(intent);
            }
        });

    }

    ActivityResultLauncher<Intent> updateLeadLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    categoryName = result.getData().getIntExtra("templateID", -1); // -1 as fallback
                    // Use templateID as needed
                    callGetFilters();
                }
            }
    );



    @Override
    public void onCardClick(int position) {
        binding.searchView.setQuery("", false);
        binding.searchView.clearFocus();
        binding.searchView.setIconified(true); // Collapse the SearchView
        binding.tvHeading.setVisibility(View.VISIBLE); // Show title again


        LeadsListModel.ContentItem contentItem = leadListModelArrayList.get(position);
        Intent intent = new Intent(mContext, UpdateLeadsActivity.class);
        intent.putExtra("templateID", categoryName);
        intent.putExtra("contentItem", contentItem); // assuming ContentItem is Serializable or Parcelable
        updateLeadLauncher.launch(intent);



    }

}
