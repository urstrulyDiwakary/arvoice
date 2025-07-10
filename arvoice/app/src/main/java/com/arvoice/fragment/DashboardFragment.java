package com.arvoice.fragment;

import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arvoice.R;
import com.arvoice.activity.AddLeadsActivity;
import com.arvoice.activity.DashboardActivity;
import com.arvoice.adapter.RoundButtonAdapter;
import com.arvoice.adapter.SummaryAdapter;
import com.arvoice.databinding.FragmentDashboardBinding;
import com.arvoice.model.SummaryItem;
import com.arvoice.model.SummaryModel;
import com.arvoice.networkinh.ApiClient;
import com.arvoice.networkinh.ApiInterface;
import com.arvoice.utils.Constant;
import com.arvoice.utils.ProgressUtils;
import com.arvoice.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class DashboardFragment extends Fragment implements RoundButtonAdapter.OnItemClickListener ,SummaryAdapter.OnSummaryClickListener{
    private FragmentDashboardBinding binding;
    List<String> buttonList = Arrays.asList("Your Leads Summary", "All Leads Summary", "Follow Up Summary");
    private RoundButtonAdapter adapter;
    private Context mContext;
    private SummaryAdapter summaryAdapter;
    private List<SummaryModel.SummaryItem> allApiData = new ArrayList<>(); // Store full API response
    ApiInterface apiInterface;
    String authToken;
    String simDetails;
    String userName;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(getLayoutInflater());
        mContext = getActivity();
        apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        getSavedToken();
        initViews();
        callGetSummary();
        requestPermissionsIfNeeded();
        return binding.getRoot();
    }
    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissionsIfNeeded() {
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_PHONE_NUMBERS,  // ✅ Required for getLine1Number()
                            Manifest.permission.READ_CALL_LOG
                    }, PERMISSION_REQUEST_CODE);
        } else {
            simDetails = getSimOperatorNames();
            createMobileSettings();

        }
    }

    private String getSimOperatorNames() {
        String sim1 = "Not Available";
        String sim2 = "Not Available";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subscriptionManager = SubscriptionManager.from(mContext);

            // Make sure permission is granted before calling this method
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "Permission not granted";
            }

            List<SubscriptionInfo> subscriptionList = subscriptionManager.getActiveSubscriptionInfoList();

            if (subscriptionList != null && !subscriptionList.isEmpty()) {
                for (int i = 0; i < subscriptionList.size(); i++) {
                    SubscriptionInfo info = subscriptionList.get(i);
                    if (i == 0) sim1 = info.getCarrierName().toString();
                    if (i == 1) sim2 = info.getCarrierName().toString();
                }
            }
        }

        return "SIM1: " + sim1 + ", SIM2: " + sim2;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                simDetails = getSimOperatorNames();
            } else {
                simDetails = "";
            }
            createMobileSettings();

        }
    }


    private void createMobileSettings(){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            String currentDateTime = sdf.format(new Date());

            String manufacturer = Build.MANUFACTURER;       // e.g., Samsung
            String model = Build.MODEL;                     // e.g., Galaxy S21
            String version = Build.VERSION.RELEASE;         // e.g., 12

            String deviceDetails = manufacturer + " " + model + ", Android " + version;

            String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);


            JSONObject json = new JSONObject();
            json.put("callTracking", "enabled");
            json.put("callRecording", "enabled");
            json.put("leadCapture", "enabled");
            json.put("callLogSync", "enabled");
            json.put("callRecordingStatus", "enabled");
            json.put("simDetails", simDetails);
            json.put("lastCallSyncTime", currentDateTime);
            json.put("deviceDetails", deviceDetails);
            json.put("deviceId", deviceId);
            json.put("appVersionSearch", "1.0.0");
            json.put("allowedPermissions", "CALL_LOG, CONTACTS, STORAGE");
            json.put("imeiRestriction", "enabled");
            json.put("permissionDetails", "CALL_LOG: granted, CONTACTS: granted, STORAGE: granted");
            json.put("userName", userName);
            json.put("ErrorLog", "No errors found");

            Log.e("Dash","Error: "+json);
            createMobileSettings(json.toString());
            // Proceed to submit this JSON via Retrofit
        } catch (Exception e) {
            Log.e("Dash","Error: "+e.getMessage());
        }
    }
    private void createMobileSettings(String criteriaJson) {
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your actual method to get token

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                criteriaJson
        );
        Call<String> call = apiInterface.createMobileSettings(token,requestBody);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("RESPONSE", "Success! Code: " + response.code());
                    Log.e("BODY", "Message: " + response.body());
                } else {
                    Log.e("BODY", "Message: " + response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
            }
        });

    }

    private void getSavedToken() {
        SharedPreferences prefs = mContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = prefs.getString("token", null); // second parameter is default value if not found
        userName = prefs.getString("userName", "");
        Log.e("Dashboard","Token"+authToken);
    }


    private void callGetSummary() {
        ProgressUtils.showProgressDialog(requireActivity());
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your actual method to get token


        Call<List<SummaryModel.SummaryItem>> call = apiInterface.getFeedCategory(token);
        call.enqueue(new Callback<List<SummaryModel.SummaryItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<SummaryModel.SummaryItem>> call, @NonNull Response<List<SummaryModel.SummaryItem>> response) {
                ProgressUtils.hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    allApiData = response.body();

                    // Print full JSON
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Log.e("Dashboard", "Response JSON:\n" + gson.toJson(allApiData));

                    // Set default category summary
                    updateSummaryForCategory("Your Leads Summary");
                } else {
                    Utils.showToast(Constant.API_CODES.ON_FAILURE_MESSAGE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SummaryModel.SummaryItem>> call, @NonNull Throwable t) {
                ProgressUtils.hideProgressDialog();
                t.printStackTrace();
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                Utils.showToast("Error: " + t.getMessage());
            }
        });
    }


    private void initViews() {
        binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        adapter = new RoundButtonAdapter(mContext, buttonList, this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        summaryAdapter = new SummaryAdapter(mContext, new ArrayList<>(), this);
        binding.summaryRecyclerView.setAdapter(summaryAdapter);
        binding.summaryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));


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
                filterSummaryList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSummaryList(newText);
                return true;
            }
        });

    }
    private void filterSummaryList(String query) {
        List<SummaryItem> filtered = new ArrayList<>();
        for (SummaryItem item : summaryAdapter.getOriginalList()) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }
        summaryAdapter.updateData(filtered);
    }

    private void updateSummaryForCategory(String selectedButton) {
        String summaryId;
        switch (selectedButton) {
            case "Your Leads Summary":
                summaryId = "users";
                break;
            case "All Leads Summary":
                summaryId = "allLeads";
                break;
            case "Follow Up Summary":
                summaryId = "followUp";
                break;
            default:
                summaryId = "";
        }


        List<SummaryItem> filteredData = new ArrayList<>();

        for (SummaryModel.SummaryItem item : allApiData) {
            if (item.getSummaryId().equalsIgnoreCase(summaryId)) {
                int templateId = item.getTemplateId();
                String title = item.getSummaryTemplateName();
                String summaryBy = item.getSummaryBy();
                String color = item.getSummaryTemplateBgColor();

                // Call once for "recordCount"
                fetchAdvancedSummary(templateId, summaryBy, title, color, filteredData, summaryId);
            }
        }

    }
    private void fetchAdvancedSummary(int templateId, String summaryBy, String title, String colorCode, List<SummaryItem> currentList, String summaryId) {
        String token = "Bearer " + authToken; // Replace with your actual method to get token

        Call<Map<String, String>> call = apiInterface.getAdvancedSummary(token,templateId, summaryBy);

        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> body = response.body();

                    String recordCount = body.get("recordCount");
                    String expectedRevenue = body.get("expectedRevenue");

                    if (recordCount == null || recordCount.isEmpty()) {
                        recordCount = "0.0";
                    }
                    if (expectedRevenue == null || expectedRevenue.isEmpty()) {
                        expectedRevenue = "0.0";
                    }

                    if (summaryBy.equalsIgnoreCase("recordCount") && body.containsKey("expectedRevenue")) {
                        currentList.add(new SummaryItem(
                                title,
                                recordCount + " Leads",
                                expectedRevenue,
                                colorCode,
                                templateId
                        ));
                    } else if (summaryBy.equalsIgnoreCase("recordCount")) {
                        currentList.add(new SummaryItem(
                                title,
                                recordCount + " Leads",
                                expectedRevenue,
                                colorCode,
                                templateId

                        ));
                    }else if (summaryBy.equalsIgnoreCase("expectedRevenue")) {
                        currentList.add(new SummaryItem(
                                title,
                                recordCount + " Leads",
                                expectedRevenue,
                                colorCode,
                                templateId

                        ));
                    }
                } else {
                    currentList.add(new SummaryItem(title, "", "0.0", colorCode,templateId
                    ));
                }

                summaryAdapter.updateFullData(currentList);

            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                Log.e("Dashboard", ">>>> " + t.getMessage(), t);
                currentList.add(new SummaryItem(title, "", "0.0", colorCode,templateId));
                summaryAdapter.updateFullData(currentList);
            }
        });
    }



    @Override
    public void onItemClick(String selected) {
        updateSummaryForCategory(selected);
    }

    @Override
    public void onSummaryClick(int templateID) {
        Log.e("Dashboard","category"+templateID);
        if (getActivity() instanceof DashboardActivity) {
            ((DashboardActivity) getActivity()).selectBottomNavItemByIndex(0); // Select 0th tab
        }

        Fragment leadFragment = new LeadFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("templateID", templateID); // Pass data (e.g., position)
        leadFragment.setArguments(bundle); // Attach bundle to fragment

        FragmentTransaction transaction = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.container, leadFragment);
        transaction.commit();
    }
}
