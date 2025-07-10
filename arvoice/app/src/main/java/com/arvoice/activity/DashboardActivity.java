package com.arvoice.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.arvoice.R;
import com.arvoice.databinding.ActivityDashboardBinding;
import com.arvoice.fragment.DashboardFragment;
import com.arvoice.fragment.DialFragment;
import com.arvoice.fragment.LeadFragment;
import com.arvoice.fragment.SiteVisitingFragment;
import com.arvoice.model.SummaryModel;
import com.arvoice.networkinh.ApiClient;
import com.arvoice.networkinh.ApiInterface;
import com.arvoice.profile.BlockListActivity;
import com.arvoice.profile.SimCardManagerActivity;
import com.arvoice.profile.UserDetailActivity;
import com.arvoice.utils.Constant;
import com.arvoice.utils.ProgressUtils;
import com.arvoice.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardActivity extends AppCompatActivity implements ExistDialog.ExistPopUp{
    public static FragmentManager fm = null;
    public static Activity activity;
    private static final String TAG_HOME = "Home";
    public static String CURRENT_TAG = TAG_HOME;
    ActivityDashboardBinding binding;

    private ActionBarDrawerToggle toggle;
    SharedPreferences prefs ;
    SharedPreferences.Editor editor;
    String authToken;
    private Context mContext;

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);
        return intent;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        editor = prefs.edit();
        activity = DashboardActivity.this;
//        setSupportActionBar(binding.toolbar);
        Log.e("Dashboard Activity", "Dashboard");

        // Setup Drawer Toggle
//        toggle = new ActionBarDrawerToggle(
//                this, binding.drawerLayout, binding.toolbar,
//                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

//        toggle.syncState();

        // Set nav drawer header values
        View headerView = binding.navView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_header_name);
        TextView navUserEmail = headerView.findViewById(R.id.nav_header_email);
        navUserName.setText(prefs.getString("userName",""));
        navUserEmail.setText(prefs.getString("email",""));

        // Set default fragment
        fm = getSupportFragmentManager();
        binding.bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        loadFragment(new DashboardFragment());

        // Bottom navigation item selection
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id =item.getItemId();
            if (id == R.id.nav_lead) {
                LeadFragment leadFragment = new LeadFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("templateID", 66);
                leadFragment.setArguments(bundle);
                openFragment(leadFragment);
            } else if (id == R.id.nav_site) {
                openFragment(new SiteVisitingFragment());
                // Handle site
            }else if (id == R.id.nav_dashboard) {
                openFragment(new DashboardFragment());
                // Handle site
            }else if (id == R.id.nav_dialer) {
                openFragment(new DialFragment());
            }
            return true;
        });

        // Drawer menu item selection
        binding.navView.setNavigationItemSelectedListener(item -> {
            int id =item.getItemId();
            if (id == R.id.nav_sim_card_manager) {
                // Handle lead
                Intent intent = new Intent(DashboardActivity.this, SimCardManagerActivity.class);
                startActivity(intent);
            }
//            else if (id == R.id.nav_blocklist) {
//                Intent intent = new Intent(DashboardActivity.this, BlockListActivity.class);
//                startActivity(intent);
//            }
//            else if (id == R.id.nav_manual_call_sync) {
//                Intent intent = new Intent(DashboardActivity.this, ManualSyncActivity.class);
//                startActivity(intent);
//
//            }
            else if (id == R.id.nav_user_details) {
                Intent intent = new Intent(DashboardActivity.this, UserDetailActivity.class);
                startActivity(intent);
            }else if (id == R.id.nav_remote_support) {
                // Handle site
                showCallChatButton();
            }else if (id == R.id.nav_logout) {
                showLogoutDialog();
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        //  Resume active fragment on backstack change
        fm.addOnBackStackChangedListener(() -> {
            Fragment currentFragment = fm.findFragmentById(R.id.container);
            if (currentFragment != null) currentFragment.onResume();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);

                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                    return;
                }

                if (!(currentFragment instanceof DashboardFragment)) {
                    binding.bottomNavigation.getMenu().getItem(2).setChecked(true);
                    openFragment(new DashboardFragment());
                } else {
                    showExistPopup();
                }
            }
        });

    }
    private void showExistPopup(){
        new ExistDialog(DashboardActivity.this, this).show();

    }
    private void loadFragment(Fragment fragment) {
        fm.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void selectBottomNavItemByIndex(int index) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (index >= 0 && index < bottomNavigationView.getMenu().size()) {
            bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(index).getItemId());
        }
    }

    @Override
    public void onBackPressed() {
        // Step 1: Close the drawer if it's open
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        // Step 2: Get the currently shown fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);

        // Step 3: If it's not Dashboard, navigate to Dashboard
        if (!(currentFragment instanceof DashboardFragment)) {
            binding.bottomNavigation.getMenu().getItem(2).setChecked(true);
            openFragment(new DashboardFragment());
        }
        // Step 4: If already on Dashboard, show Exit Dialog
        else {
            new ExistDialog(this, this).show(); // show exit confirmation
        }

        // ⚠️ Do NOT call super.onBackPressed() here — it will close the activity
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        fragment.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onPause() {
        invalidateOptionsMenu();
        super.onPause();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");

        builder.setMessage("Choose an option:");

        builder.setPositiveButton("Logout", (dialog, which) -> {
            // Handle single device logout
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(DashboardActivity.this, SplashActivity.class);
            startActivity(intent);
            finish(); //
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> {
            // Handle logout from all devices
            dialog.dismiss(); // Simply close the dialog

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showCallChatButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remote Support");

        builder.setMessage("Choose an option for remote support:");

        builder.setNegativeButton("CALL", (dialog, which) -> {
            String phoneNumber = "917659872496";
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);

        });

        builder.setPositiveButton("CHAT", (dialog, which) -> {
            String phoneNumber = "917659872496";
            String url = "https://wa.me/" + phoneNumber;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setPackage("com.whatsapp");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("CANCEL", (dialog, which) -> {
            // Handle logout from all devices
            dialog.dismiss(); // Simply close the dialog

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void callLogoutFromAllDevices() {
        ProgressUtils.showProgressDialog(this);
        authToken = prefs.getString("token", null);
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your actual method to get token


        Call<List<SummaryModel.SummaryItem>> call = apiInterface.getFeedCategory(token);
        call.enqueue(new Callback<List<SummaryModel.SummaryItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<SummaryModel.SummaryItem>> call, @NonNull Response<List<SummaryModel.SummaryItem>> response) {
                ProgressUtils.hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {

                    editor.clear();
                    editor.commit();

                    Intent intent = new Intent(DashboardActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
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

    @Override
    public void onYesClicked() {
        finish();
    }
}