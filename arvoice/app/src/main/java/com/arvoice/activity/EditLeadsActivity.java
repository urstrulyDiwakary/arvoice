package com.arvoice.activity;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arvoice.R;
import com.arvoice.databinding.ActivityAddLeadBinding;
import com.arvoice.databinding.ActivityEditLeadBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Calendar;


public class EditLeadsActivity extends AppCompatActivity{
    private Context mContext;
    private ActivityEditLeadBinding binding;
    private int selectedLeadStateIndex = -1; // Track selected item
    private int selectedCatgeoryIndex = -1; // Track selected item
    private int selectedSourceIndex = -1; // Track selected item
    final boolean[] isChecked = {false}; // To track state

    Calendar calendar;


    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, EditLeadsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditLeadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        calendar = Calendar.getInstance();

        diabled();
        listener();
    }
    private void enabled() {
        binding.etContactName.setEnabled(true);
        binding.etMobileNumber.setEnabled(true);
        binding.etLeadOwner.setEnabled(true);
        binding.etAlternate.setEnabled(true);
        binding.etEmail.setEnabled(true);
        binding.etExpectedRevenuew.setEnabled(true);
        binding.etExpectedClosing.setEnabled(true);
        binding.etLeadStage.setEnabled(true);
        binding.etCategory.setEnabled(true);
        binding.etFoolowUp.setEnabled(true);
        binding.etFollowNotes.setEnabled(true);
        binding.etAbout.setEnabled(true);
        binding.etLeadSource.setEnabled(true);
        binding.etCampaignName.setEnabled(true);
        binding.etCampaignTerm.setEnabled(true);
        binding.etetCampaignContent.setEnabled(true);
        binding.etCallerName.setEnabled(true);
        binding.etLastCall.setEnabled(true);
        binding.etCallDuration.setEnabled(true);
        binding.etCallStatus.setEnabled(true);
        binding.etReason.setEnabled(true);
    }
    private void diabled() {
        binding.etContactName.setEnabled(false);
        binding.etMobileNumber.setEnabled(false);
        binding.etLeadOwner.setEnabled(false);
        binding.etAlternate.setEnabled(false);
        binding.etEmail.setEnabled(false);
        binding.etExpectedRevenuew.setEnabled(false);
        binding.etExpectedClosing.setEnabled(false);
        binding.etLeadStage.setEnabled(false);
        binding.etCategory.setEnabled(false);
        binding.etFoolowUp.setEnabled(false);
        binding.etFollowNotes.setEnabled(false);
        binding.etAbout.setEnabled(false);
        binding.etLeadSource.setEnabled(false);
        binding.etCampaignName.setEnabled(false);
        binding.etCampaignTerm.setEnabled(false);
        binding.etetCampaignContent.setEnabled(false);
        binding.etCallerName.setEnabled(false);
        binding.etLastCall.setEnabled(false);
        binding.etCallDuration.setEnabled(false);
        binding.etCallStatus.setEnabled(false);
        binding.etReason.setEnabled(false);
    }

    private void listener() {
        binding.ivEdit.setOnClickListener(v -> {
            if (isChecked[0]) {
                binding.ivEdit.animate().alpha(0f).setDuration(100).withEndAction(() -> {
                    binding.ivEdit.setImageResource(R.drawable.baseline_edit_24); // Default icon
                    binding.ivEdit.animate().alpha(1f).setDuration(100).start();
                }).start();
                diabled();
            } else {
                binding.ivEdit.animate().alpha(0f).setDuration(100).withEndAction(() -> {
                    binding.ivEdit.setImageResource(R.drawable.baseline_check_24); // Alternate icon
                    binding.ivEdit.animate().alpha(1f).setDuration(100).start();
                }).start();
                enabled();
            }
            isChecked[0] = !isChecked[0]; // Toggle state
        });
        binding.topbar.setOnClickListener(view -> finish());
        binding.etExpectedClosing.setOnClickListener(view -> showDatePicker("0"));
        binding.etFoolowUp.setOnClickListener(view -> showDatePicker("1"));
        binding.etLeadStage.setOnClickListener(view -> showLeadStateBottomSheet());
        binding.etCategory.setOnClickListener(view -> showCategoryBottomSheet());
        binding.etLeadSource.setOnClickListener(view -> showLeadSourceBottomSheet());
    }
    private void showLeadSourceBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottomsheet_category, null);
        bottomSheetDialog.setContentView(sheetView);

        ListView listView = sheetView.findViewById(R.id.categoryListView);
        TextView title = sheetView.findViewById(R.id.tvTitle);
        title.setText("Select Lead Source");
        ImageView close = sheetView.findViewById(R.id.ivClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        String[] categories = {
                "Incoming Call", "WhatsApp", "Website", "Facebook Ad",
                "Google Ad", "Indiamart", "Tradeindia", "Justdial", "Sulekha", "Paper Ad",
                "Cold Calling", "Reference", "olx", "old data"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_lead_state, R.id.textItem, categories) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView checkIcon = view.findViewById(R.id.checkIcon);
                if (position == selectedSourceIndex) {
                    checkIcon.setVisibility(View.VISIBLE);
                } else {
                    checkIcon.setVisibility(View.GONE);
                }
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedSourceIndex = position;
            binding.etLeadSource.setText(categories[position]);
            adapter.notifyDataSetChanged(); // Refresh list to show tick
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }
    private void showCategoryBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottomsheet_category, null);
        bottomSheetDialog.setContentView(sheetView);

        ListView listView = sheetView.findViewById(R.id.categoryListView);
        TextView title = sheetView.findViewById(R.id.tvTitle);
        title.setText("Select Category");
        ImageView close = sheetView.findViewById(R.id.ivClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        String[] categories = {
                "Lawyers", "Teachers", "Doctors", "Business", "software data", "insurance data",
                "salaried professionals", "Business owners/entrepreneurs", "Government Employees",
                "Students", "IT Professionals", "Farmers/landowners", "Freelancers", "Others", "OLD DATA"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_lead_state, R.id.textItem, categories) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView checkIcon = view.findViewById(R.id.checkIcon);
                if (position == selectedCatgeoryIndex) {
                    checkIcon.setVisibility(View.VISIBLE);
                } else {
                    checkIcon.setVisibility(View.GONE);
                }
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedCatgeoryIndex = position;
            binding.etCategory.setText(categories[position]);
            adapter.notifyDataSetChanged(); // Refresh list to show tick
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }
    private void showLeadStateBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottomsheet_category, null);
        bottomSheetDialog.setContentView(sheetView);

        ListView listView = sheetView.findViewById(R.id.categoryListView);
        ImageView close = sheetView.findViewById(R.id.ivClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        String[] categories = {
                "New", "Contacted", "Interested", "Follow Up", "Deal Closed", "Cancel Deal", "Not Interested","Site Visit"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_lead_state, R.id.textItem, categories) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView checkIcon = view.findViewById(R.id.checkIcon);
                if (position == selectedLeadStateIndex) {
                    checkIcon.setVisibility(View.VISIBLE);
                } else {
                    checkIcon.setVisibility(View.GONE);
                }
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedLeadStateIndex = position;
            binding.etLeadStage.setText(categories[position]);
            adapter.notifyDataSetChanged(); // Refresh list to show tick
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }


    private void showDatePicker(String from) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Note: 0-based (Jan = 0)
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.MyDatePickerDialogTheme,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    if (from.equalsIgnoreCase("0")){
                        binding.etExpectedClosing.setText(date);
                    }if (from.equalsIgnoreCase("1")){
                        binding.etFoolowUp.setText(date);
                    }
                },
                year, month, day
        );
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }



}
