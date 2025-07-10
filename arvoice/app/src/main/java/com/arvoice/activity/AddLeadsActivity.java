package com.arvoice.activity;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.arvoice.R;
import com.arvoice.databinding.ActivityAddLeadBinding;
import com.arvoice.model.Field;
import com.arvoice.model.Group;
import com.arvoice.model.LeadCreate;
import com.arvoice.model.UserModel;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddLeadsActivity extends AppCompatActivity{
    private Context mContext;
    private ActivityAddLeadBinding binding;
    private final List<FieldEntry> fieldEntries = new ArrayList<>();
    JSONObject leadInfoExtnAttr = new JSONObject();
    String authToken;
    String userName;
    String email;
    int templateID;
    String number;
    private final Map<String, String> preservedValues = new HashMap<>();
    private String selectedVisitType = "";
    private EditText selectedEditText; // Keep track of the current EditText being updated
    private ActivityResultLauncher<Intent> userResultLauncher;
    private String selectedLeadStage = "";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLeadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        if(getIntent()!=null){
            number = getIntent().getStringExtra("number");
            templateID = getIntent().getIntExtra("templateID",templateID);
        }

        getSavedToken();
        callGetForm();
        listener();
        userResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        UserModel selectedUser = (UserModel) result.getData().getSerializableExtra("selectedUser");
                        if (selectedUser != null && selectedEditText != null) {
                            selectedEditText.setText(selectedUser.getUsername());
                            selectedEditText.setError(null); // clear error
                        }
                    }
                });
    }

    private void getSavedToken() {
        SharedPreferences prefs = mContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = prefs.getString("token", null); // second parameter is default value if not found
        userName = prefs.getString("userName", null); // second parameter is default value if not found
        email = prefs.getString("email", null); // second parameter is default value if not found
        Log.e("Add Lead","****"+userName);
    }

    private void callGetForm() {
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your actual method to get token

        Call<LeadCreate> call = apiInterface.getLeadForm(token);
        call.enqueue(new Callback<LeadCreate>() {
            @Override
            public void onResponse(@NonNull Call<LeadCreate> call, @NonNull Response<LeadCreate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LeadCreate leadCreate = response.body();

                    // clear previous views if reloading
                    binding.formLayout.removeAllViews();

                    renderFormUI(leadCreate); // ← this method builds the UI dynamically
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
    private void renderFormUI(LeadCreate leadResponse) {
        binding.formLayout.removeAllViews(); // ✅ clear previous views
        fieldEntries.clear();
        for (Group group : leadResponse.getGroups()) {

            // 🔹 Create CardView
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, dpToPx(24)); // bottom margin between cards
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(dpToPx(8));
            cardView.setCardElevation(dpToPx(4));

            // 🔹 Container layout inside CardView
            LinearLayout cardInnerLayout = new LinearLayout(this);
            cardInnerLayout.setOrientation(LinearLayout.VERTICAL);
            cardInnerLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

            // 🔹 Group Title
            TextView sectionTitle = new TextView(this);
            sectionTitle.setText(group.getLabel());
            sectionTitle.setTextSize(18);
            sectionTitle.setTextColor(Color.BLACK);
            sectionTitle.setTypeface(null, Typeface.BOLD);
            sectionTitle.setPadding(0, 0, 0, dpToPx(16));
            cardInnerLayout.addView(sectionTitle);

            for (Field field : group.getFields()) {
                if (!field.isVisible()) continue;

                String rawLabel = field.getLabel();
                String label = rawLabel != null ? rawLabel.trim() : "";
                boolean isRequired = field.isRequired();
                String type = field.getType() != null ? field.getType().trim() : "";

                Log.d("UpdateLead", "selected"+selectedVisitType.trim());

                if ("Assigned Manager".equalsIgnoreCase(label)) {
                    if (selectedVisitType != null && !"SITE VISIT".equalsIgnoreCase(selectedVisitType.trim())) {
                        Log.d("UpdateLead", "Hiding Assigned Manager because visit type is not SITE VISIT");
                        continue;
                    }
                }


                // 🔹 Label
                TextView labelView = new TextView(this);
                if (isRequired) {
                    SpannableString spannableLabel = new SpannableString(label + " *");
                    spannableLabel.setSpan(
                            new ForegroundColorSpan(Color.RED),
                            spannableLabel.length() - 1,
                            spannableLabel.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    labelView.setText(spannableLabel);
                } else {
                    labelView.setText(label);
                }
                labelView.setTypeface(null, Typeface.BOLD);


                LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                labelParams.topMargin = dpToPx(16); // space between fields
                labelView.setLayoutParams(labelParams);
                cardInnerLayout.addView(labelView);

                // 🔹 LayoutParams for input fields
                LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                inputParams.topMargin = dpToPx(8); // space between label and field

                switch (type) {
                    case "user": {
                        EditText editText = new EditText(this);
                        editText.setLayoutParams(inputParams);
                        editText.setTextColor(ContextCompat.getColor(this, R.color.black));
                        editText.setFocusable(false); // 🔒 Prevent keyboard
                        editText.setInputType(InputType.TYPE_NULL);

                        Drawable dropdownIcon = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_drop_down_24);
                        if (dropdownIcon != null) {
                            dropdownIcon = DrawableCompat.wrap(dropdownIcon);
                            DrawableCompat.setTint(dropdownIcon, ContextCompat.getColor(this, R.color.colorPrimary));
                        }
                        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, dropdownIcon, null);
                        editText.setCompoundDrawablePadding(dpToPx(8));
                        String saved = preservedValues.get(label);
                        if (saved != null) {
                            editText.setText(saved); // ✅ Use saved if user changed before
                        } else if ("Lead Owner".equalsIgnoreCase(label)) {
                            editText.setText(userName); // ✅ Default to userName only for Lead Owner
                        }

                        editText.setOnClickListener(v -> {
                            // Open a dialog or do something
                            selectedEditText = editText; // 👈 So we know where to put the result
                            Intent intent = new Intent(this, SelectUserActivity.class);
                            userResultLauncher.launch(intent);
                        });

                        cardInnerLayout.addView(editText);
                        fieldEntries.add(new FieldEntry(label, isRequired, editText, field));
                        break;
                    }
                    case "text":
                    case "email":
                    case "textarea": {
                        EditText editText = new EditText(this);
                        if (type.equals("email")) {
                            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            editText.setText(email); //
                            editText.setEnabled(field.isEditable());  // 🔸 Make editable or not
                            String saved = preservedValues.get(label);
                            if (saved != null) {
                                editText.setText(saved);
                            }
                        } else if ("text".equals(type)){
                            editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                            String saved = preservedValues.get(label);
                            if (saved != null) {
                                editText.setText(saved);
                            }
                        }
                        else if (type.equals("phone")) {
                            editText.setInputType(InputType.TYPE_CLASS_PHONE);
                            editText.setEnabled(field.isEditable());  // 🔸 Make editable or not
                            String saved = preservedValues.get(label);
                            if (saved != null) {
                                editText.setText(saved);
                            }
                        } else if (type.equals("textarea")) {
                            editText.setMinLines(3);
                            editText.setGravity(Gravity.TOP);

                            boolean enable;
                            if ("dealDescription".equalsIgnoreCase(field.getName())) {
                                enable = "DEAL CLOSED".equalsIgnoreCase(selectedLeadStage);
                            } else if ("lostDescription".equalsIgnoreCase(field.getName())) {
                                enable = "CANCEL DEAL".equalsIgnoreCase(selectedLeadStage);
                            } else {
                                enable = field.isEditable();
                            }

                            editText.setEnabled(enable);
                            editText.setFocusable(enable);
                            editText.setFocusableInTouchMode(enable);

                            if (enable && preservedValues.get(label) != null) {
                                editText.setText(preservedValues.get(label)); // Only set value if field is editable
                            } else if (!enable) {
                                editText.setText(""); // Clear if not editable
                            }
                        }



                        editText.setLayoutParams(inputParams);
                        cardInnerLayout.addView(editText);
                        fieldEntries.add(new FieldEntry(label, isRequired, editText,field));
                        break;
                    }
                    case "phone": {
                        LinearLayout rowLayout = new LinearLayout(this);
                        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        rowLayout.setGravity(Gravity.CENTER_VERTICAL);

                        // Create EditText
                        EditText phoneField = new EditText(this);
                        phoneField.setInputType(InputType.TYPE_CLASS_NUMBER);
                        phoneField.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(13) });

                        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                1f
                        );
                        phoneField.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                Log.e("Label","Label0**"+label);
                                Log.e("Label","Label1**"+s.toString());
                                for (FieldEntry entry : fieldEntries) {
                                    EditText viewField = entry.inputField;
                                    if (viewField != null) {
                                        preservedValues.put(entry.label, viewField.getText().toString().trim());
                                    }
                                }
                                preservedValues.put(label, s.toString()); // Save typed value
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        String saved = preservedValues.get(label);
                        Log.e("Label","Label2**"+label);
                        if (saved != null) {
                            phoneField.setText(saved);
                        }

                        editTextParams.setMarginEnd(8);
                        phoneField.setLayoutParams(editTextParams);

                        // Add EditText to row
                        rowLayout.addView(phoneField);

                        // If label is "mobileNumber", add 3 ImageViews
                        if (label.equalsIgnoreCase("Mobile Number")) {
                            int[] icons = { R.drawable.call, R.drawable.whatsapp, R.drawable.baseline_message_24 }; // Replace with your actual drawables
                            if (saved == null || saved.isEmpty()) {
                                phoneField.setText(number); // Only set default if nothing is preserved
                            }

                            for (int i = 0; i < icons.length; i++) {
                                int icon = icons[i];
                                ImageView iconView = new ImageView(this);
                                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                                        dpToPx(36),
                                        dpToPx(36)
                                );
                                iconParams.setMarginEnd(dpToPx(8));
                                iconView.setLayoutParams(iconParams);
                                iconView.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
                                iconView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_bg));
                                iconView.setImageResource(icon);
                                iconView.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);

                                if (i == 0) {
                                    iconView.setOnClickListener(v -> {
                                        String number = phoneField.getText().toString().trim();
                                        if (number.isEmpty()) {
                                            Toast.makeText(mContext, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

                                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
                                            return;
                                        }

                                        List<SubscriptionInfo> subscriptionList = subscriptionManager.getActiveSubscriptionInfoList();

                                        if (subscriptionList == null || subscriptionList.isEmpty()) {
                                            Toast.makeText(mContext, "No SIM card found", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        String[] simNames = new String[subscriptionList.size()];
                                        for (int j = 0; j < subscriptionList.size(); j++) {
                                            SubscriptionInfo info = subscriptionList.get(j);
                                            int slotIndex = info.getSimSlotIndex(); // 0 or 1
                                            String carrierName = info.getDisplayName().toString();
                                            simNames[j] = "SIM " + (slotIndex + 1) + " - " + carrierName;
                                        }

                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Choose SIM");
                                        builder.setItems(simNames, (dialog, which) -> {
                                            int simSlot = subscriptionList.get(which).getSimSlotIndex(); // get actual slot
                                            if (saved != null && !saved.isEmpty()) {
                                                makeCall(saved, simSlot);
                                                Log.e("L", "Make1: using saved = " + saved);
                                            } else {
                                                makeCall(number, simSlot);
                                                Log.e("L", "Make1: using number = " + number);
                                            }


                                        });

                                        builder.show();
                                    });
                                }

                                // 💬 WhatsApp
                                else if (i == 1) {
                                    iconView.setOnClickListener(v -> {
                                        String number = phoneField.getText().toString().trim();
                                        if (!number.isEmpty()) {
                                            String fullNumber = "+91" + number; // Update country code if needed
                                            String url = "https://wa.me/" + fullNumber;

                                            try {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse(url));
                                                mContext.startActivity(intent);
                                            } catch (Exception e) {
                                                Toast.makeText(mContext, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(mContext, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                // ✉️ SMS
                                else if (i == 2) {
                                    iconView.setOnClickListener(v -> {
                                        String number = phoneField.getText().toString().trim();
                                        if (!number.isEmpty()) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number));
                                            intent.putExtra("sms_body", ""); // Optional: pre-fill message
                                            mContext.startActivity(intent);
                                        } else {
                                            Toast.makeText(mContext, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                // Optionally: set click listeners here
                                // iconView.setOnClickListener(...)

                                rowLayout.addView(iconView);
                            }
                        }
                        phoneField.setEnabled(field.isEditable());  // 🔸 Make editable or not

                        // Add the entire row to your cardInnerLayout
                        cardInnerLayout.addView(rowLayout);

                        // Add field entry
                        fieldEntries.add(new FieldEntry(label, isRequired, phoneField,field));
                        break;
                    }
                    case "number": {
                        EditText numberField = new EditText(this);
                        numberField.setInputType(InputType.TYPE_CLASS_NUMBER);
                        numberField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
                        numberField.setLayoutParams(inputParams);


                        boolean enable;
                        if ("dealPrice".equalsIgnoreCase(field.getName()) || "dealNoOfCents".equalsIgnoreCase(field.getName()) || "dealTotalValue".equalsIgnoreCase(field.getName())) {
                            enable = "DEAL CLOSED".equalsIgnoreCase(selectedLeadStage);
                        } else {
                            enable = field.isEditable();
                        }

                        numberField.setEnabled(enable);
                        numberField.setFocusable(enable);
                        numberField.setFocusableInTouchMode(enable);

                        if (enable && preservedValues.get(label) != null) {
                            numberField.setText(preservedValues.get(label)); // Only set value if field is editable
                        } else if (!enable) {
                            numberField.setText(""); // Clear if not editable
                        }
                        numberField.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                for (FieldEntry entry : fieldEntries) {
                                    EditText viewField = entry.inputField;
                                    if (viewField != null) {
                                        preservedValues.put(entry.label, viewField.getText().toString().trim());
                                    }
                                }
                                preservedValues.put(label, s.toString()); // Save typed value
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        cardInnerLayout.addView(numberField);
                        fieldEntries.add(new FieldEntry(label, isRequired, numberField,field));
                        break;
                    }


                    case "date":{
                        EditText dateEditText = new EditText(this);
                        dateEditText.setHint("Select Date");
                        dateEditText.setInputType(InputType.TYPE_NULL);
                        dateEditText.setLayoutParams(inputParams);

                        // 🔹 Add calendar icon at the end
                        Drawable calendarIcon = ContextCompat.getDrawable(this, R.drawable.baseline_calendar_month_24);
                        if (calendarIcon != null) {
                            calendarIcon = DrawableCompat.wrap(calendarIcon);
                            DrawableCompat.setTint(calendarIcon, ContextCompat.getColor(this, R.color.colorPrimary)); // Replace with your color resource
                        }
                        dateEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, calendarIcon, null);
                        dateEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, calendarIcon, null);
                        dateEditText.setCompoundDrawablePadding(16);

                        boolean enable;

                        if ("dealDate".equalsIgnoreCase(field.getName())) {
                            enable = "DEAL CLOSED".equalsIgnoreCase(selectedLeadStage);
                        } else if ("lostDate".equalsIgnoreCase(field.getName())) {
                            enable = "CANCEL DEAL".equalsIgnoreCase(selectedLeadStage);
                        } else {
                            enable = field.isEditable();
                        }


                        dateEditText.setEnabled(enable);
                        dateEditText.setFocusable(enable);
                        dateEditText.setFocusableInTouchMode(enable);

                        if (enable && preservedValues.get(label) != null) {
                            dateEditText.setText(preservedValues.get(label));
                        } else {
                            dateEditText.setText(""); // Clear value when disabled
                        }


                        // 🔹 Show DatePickerDialog on click
                        dateEditText.setOnClickListener(v -> {
                            final Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                                    (view, selectedYear, selectedMonth, selectedDay) -> {
                                        // Zero-padded month and day
                                        String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                                        dateEditText.setText(selectedDate);
                                    }, year, month, day);
                            datePickerDialog.show();
                        });


                        cardInnerLayout.addView(dateEditText);
                        fieldEntries.add(new FieldEntry(label, isRequired, dateEditText,field));

                        break;
                    }
                    case "dateTime": {
                        EditText dateTimeEditText = new EditText(this);
                        dateTimeEditText.setFocusable(false);
                        dateTimeEditText.setHint("Select Date & Time");
                        dateTimeEditText.setInputType(InputType.TYPE_NULL);
                        dateTimeEditText.setLayoutParams(inputParams);
                        dateTimeEditText.setPadding(24, 24, 24, 24);

                        // 🔹 Add calendar icon with tint
                        Drawable calendarIcon = ContextCompat.getDrawable(this, R.drawable.baseline_calendar_month_24);
                        if (calendarIcon != null) {
                            calendarIcon = DrawableCompat.wrap(calendarIcon);
                            DrawableCompat.setTint(calendarIcon, ContextCompat.getColor(this, R.color.colorPrimary));
                        }
                        dateTimeEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, calendarIcon, null);
                        dateTimeEditText.setCompoundDrawablePadding(16);

                        // 🔹 Show Date & Time Picker
                        dateTimeEditText.setOnClickListener(v -> {
                            final Calendar calendar = Calendar.getInstance();

                            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                                    (view, year, month, dayOfMonth) -> {
                                        final int selectedYear = year;
                                        final int selectedMonth = month;
                                        final int selectedDay = dayOfMonth;

                                        // After date is picked, open time picker
                                        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                                                (timeView, hourOfDay, minute) -> {
                                                    Calendar selectedDateTime = Calendar.getInstance();
                                                    selectedDateTime.set(selectedYear, selectedMonth, selectedDay, hourOfDay, minute);

                                                    SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.getDefault());
                                                    String formattedDisplay = displayFormat.format(selectedDateTime.getTime());
                                                    dateTimeEditText.setText(formattedDisplay.replace("\\/", "/"));

                                                    SimpleDateFormat sendFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                                                    String formattedSend = sendFormat.format(selectedDateTime.getTime());

                                                    try {
                                                        leadInfoExtnAttr.put("nextFollow-upOn", formattedSend);
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
                        dateTimeEditText.setEnabled(field.isEditable());  // 🔸 Make editable or not
                        String saved = preservedValues.get(label);
                        if (saved != null) {
                            dateTimeEditText.setText(saved);
                        }
                        cardInnerLayout.addView(dateTimeEditText);
                        fieldEntries.add(new FieldEntry(label, isRequired, dateTimeEditText,field));

                        break;
                    }



                    case "select": {
                        EditText selectEditText = new EditText(this);
                        selectEditText.setFocusable(false);
                        selectEditText.setHint("Select option");
                        selectEditText.setInputType(InputType.TYPE_NULL);
                        selectEditText.setLayoutParams(inputParams);

                        // Add dropdown icon
                        Drawable dropdownIcon = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_drop_down_24);
                        if (dropdownIcon != null) {
                            dropdownIcon = DrawableCompat.wrap(dropdownIcon);
                            DrawableCompat.setTint(dropdownIcon, ContextCompat.getColor(this, R.color.colorPrimary));
                        }
                        selectEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, dropdownIcon, null);
                        selectEditText.setCompoundDrawablePadding(dpToPx(8));

                        // Populate options
                        List<String> options = Arrays.stream(field.getOptions().split("\\|"))
                                .map(String::trim)
                                .collect(Collectors.toList());

                        String saved = preservedValues.get(label);
                        if (saved != null) {
                            selectEditText.setText(saved);
                        }

                        // Handle selection
                        selectEditText.setOnClickListener(v -> {
                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
                            View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null);

                            ListView listView = sheetView.findViewById(R.id.listViewOptions);
                            ArrayAdapter<String> bottomSheetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
                            listView.setAdapter(bottomSheetAdapter);

                            listView.setOnItemClickListener((parent, view, position, id) -> {
                                String selected = options.get(position).trim();
                                preservedValues.put(label, selected);
                                selectEditText.setText(selected);

                                // ✅ Update lead stage first
                                if ("Lead Stage".equalsIgnoreCase(label)) {
                                    selectedLeadStage = selected;
                                }

                                // ✅ Re-check all fields like "lostReason"
                                for (FieldEntry entry : fieldEntries) {
                                    EditText viewField = entry.inputField;
                                    if (viewField != null) {
                                        preservedValues.put(entry.label, viewField.getText().toString().trim());
                                    }

                                    String fieldName = entry.field.getName();
                                    boolean enable;

                                    // Special logic for fields based on Lead Stage
                                    if ("lostReason".equalsIgnoreCase(fieldName)) {
                                        enable = "CANCEL DEAL".equalsIgnoreCase(selectedLeadStage);
                                    } else if ("dealDate".equalsIgnoreCase(fieldName) || "dealPrice".equalsIgnoreCase(fieldName)
                                            || "dealNoOfCents".equalsIgnoreCase(fieldName)) {
                                        enable = "DEAL CLOSED".equalsIgnoreCase(selectedLeadStage);
                                    } else {
                                        enable = entry.field.isEditable();
                                    }

                                    viewField.setEnabled(enable);
                                    viewField.setFocusable(enable);
                                    viewField.setFocusableInTouchMode(enable);
                                    viewField.setAlpha(enable ? 1.0f : 0.5f);
                                }

                                bottomSheetDialog.dismiss();
                                renderFormUI(leadResponse);
                            });

                            bottomSheetDialog.setContentView(sheetView);
                            bottomSheetDialog.show();
                        });

                        // Initial enable/disable based on logic
                        boolean enable;
                        String fieldName = field.getName();
                        if ("lostReason".equalsIgnoreCase(fieldName)) {
                            enable = "CANCEL DEAL".equalsIgnoreCase(selectedLeadStage);
                        } else if ("dealDate".equalsIgnoreCase(fieldName) || "dealPrice".equalsIgnoreCase(fieldName)
                                || "dealNoOfCents".equalsIgnoreCase(fieldName)) {
                            enable = "DEAL CLOSED".equalsIgnoreCase(selectedLeadStage);
                        } else {
                            enable = field.isEditable();
                        }

                        selectEditText.setEnabled(enable);
                        selectEditText.setFocusable(enable);
                        selectEditText.setFocusableInTouchMode(enable);
                        selectEditText.setAlpha(enable ? 1.0f : 0.5f);

                        cardInnerLayout.addView(selectEditText);
                        fieldEntries.add(new FieldEntry(label, isRequired, selectEditText, field));

                        break;
                    }

                }
            }

            cardView.addView(cardInnerLayout);
            binding.formLayout.addView(cardView);
        }
    }


    private void makeCall(String number, int simSlot) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_PHONE_NUMBERS,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CALL_PHONE
                    },
                    1001); // Request code
            return;
        }

        if (number.isEmpty()) {
            Utils.showToast("Please enter a number");
            return;
        }

        TelecomManager telecomManager = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        SubscriptionManager subscriptionManager = (SubscriptionManager) mContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddLeadsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddLeadsActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
            return;
        }

        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (subscriptionInfoList != null && subscriptionInfoList.size() > simSlot) {
            SubscriptionInfo subscriptionInfo = subscriptionInfoList.get(simSlot);

            PhoneAccountHandle phoneAccountHandle = null;
            for (PhoneAccountHandle handle : telecomManager.getCallCapablePhoneAccounts()) {
                if (handle.getId().contains(String.valueOf(subscriptionInfo.getSubscriptionId()))) {
                    phoneAccountHandle = handle;
                    break;
                }
            }

            if (phoneAccountHandle != null) {
                Bundle extras = new Bundle();
                extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);

                Uri uri = Uri.fromParts("tel", number, null);
                telecomManager.placeCall(uri, extras);
            } else {
                Utils.showToast("Unable to find SIM slot.");
            }
        } else {
            Utils.showToast("Invalid SIM slot");
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void listener() {
        binding.topbar.setOnClickListener(view -> finish());
        binding.ivDone.setOnClickListener(view -> {
            for (FieldEntry entry : fieldEntries) {
                if (entry.isRequired && entry.inputField.getText().toString().trim().isEmpty()) {
                    Utils.showToast(entry.label + " is required");
                    return;
                }
            }
            if(getFieldValue("Lead Stage").equalsIgnoreCase("DEAL CLOSED")){
                if(getFieldValue("Deal Date").isEmpty()){
                    Utils.showToast("Please enter Deal Date");
                    return;
                }
                if(getFieldValue("Deal Price").isEmpty()){
                    Utils.showToast("Please enter Deal Price");
                    return;
                }
                if(getFieldValue("Deal No Of Cents").isEmpty()){
                    Utils.showToast("Please enter Deal No Of Cents");
                    return;
                }
                if(getFieldValue("Deal Total Value").isEmpty()){
                    Utils.showToast("Please enter Deal Total Value");
                    return;
                }if(getFieldValue("Deal Description").isEmpty()){
                    Utils.showToast("Please enter Deal Description");
                    return;
                }

            }
            if(getFieldValue("Lead Stage").equalsIgnoreCase("CANCEL DEAL")){
                if(getFieldValue("Lost Date").isEmpty()){
                    Utils.showToast("Please enter Lost Date");
                    return;
                }
                if(getFieldValue("Lost Reason").isEmpty()){
                    Utils.showToast("Please enter Lost Reason");
                    return;
                }
                if(getFieldValue("Lost Description").isEmpty()){
                    Utils.showToast("Please enter Lost Description");
                    return;
                }
            }


            try {
                JSONObject json = new JSONObject();
                json.put("leadOwner", getFieldValue("Lead Owner"));
                json.put("contactName", getFieldValue("Contact Name"));
                json.put("mobileNumber", getFieldValue("Mobile Number"));
                json.put("alternateNumber", getFieldValue("Alternate Number"));
                json.put("emailAddress", getFieldValue("Email Address"));
                json.put("leadStage", getFieldValue("Lead Stage"));
                json.put("expectedRevenue", getFieldValue("Expected Revenue"));
                json.put("expectedClosingDate", getFieldValue("Expected Closing Date")); // Format properly if using dynamic date
                json.put("siteVisited", getFieldValue("Site Visited"));
                json.put("leadTitle", getFieldValue("Lead Title"));
                json.put("category", getFieldValue("Category"));
                json.put("assignedManager", getFieldValue("Assigned Manager"));
                json.put("leadDate", getFieldValue("Lead Date"));
                json.put("leadOwnerEmail", getFieldValue("Lead Owner Email"));


                leadInfoExtnAttr.put("nextFollow-upOn",getFieldValue("Next Follow-Up On") );
                leadInfoExtnAttr.put("nextFollow-upNotes",getFieldValue("Next Follow-up Notes") );
                leadInfoExtnAttr.put("description", getFieldValue("Update few words about this lead"));
                leadInfoExtnAttr.put("gender", getFieldValue("Gender"));
                if(getFieldValue("Lead Stage").equalsIgnoreCase("CANCEL DEAL")){
                    leadInfoExtnAttr.put("lostDate", getFieldValue("Lost Date"));
                    leadInfoExtnAttr.put("lostReason", getFieldValue("Lost Reason"));
                    leadInfoExtnAttr.put("lostDescription", getFieldValue("Lost Description"));
                }else{
                    leadInfoExtnAttr.put("lostDate", "");
                    leadInfoExtnAttr.put("lostReason", "");
                    leadInfoExtnAttr.put("lostDescription", "");
                }
                json.put("leadInfoExtnAttr", leadInfoExtnAttr);

                JSONObject sourceInfo = new JSONObject();
                sourceInfo.put("leadSource", getFieldValue("Lead Source"));
                sourceInfo.put("campaignName", getFieldValue("Campaign Name"));
                sourceInfo.put("campaignTeam", getFieldValue("Campaign Team"));
                sourceInfo.put("campaignContent", getFieldValue("Campaign Content"));
                sourceInfo.put("createdBy", getFieldValue("Created By"));
                sourceInfo.put("sourceInfoExtnAttr", new JSONObject());
                json.put("sourceInfo", sourceInfo);

                JSONObject wonInfo = new JSONObject();
                if(getFieldValue("Lead Stage").equalsIgnoreCase("DEAL CLOSED")){
                    wonInfo.put("dealDate", getFieldValue("Deal Date"));
                    wonInfo.put("dealPrice", getFieldValue("Deal Price"));
                    wonInfo.put("dealNoOfCents", getFieldValue("Deal No Of Cents"));
                    wonInfo.put("dealTotalValue", getFieldValue("Deal Total Value"));
                    wonInfo.put("dealDescription", getFieldValue("Deal Description"));
                    wonInfo.put("wonInfoExtnAttr", new JSONObject());
                }else{
                    wonInfo.put("dealDate", "");
                    wonInfo.put("dealPrice", "");
                    wonInfo.put("dealNoOfCents", "");
                    wonInfo.put("dealTotalValue", "");
                    wonInfo.put("dealDescription", "");
                    wonInfo.put("wonInfoExtnAttr", new JSONObject());
                }
                json.put("wonInfo", wonInfo);

                JSONObject leadHistory = new JSONObject();
                leadHistory.put("leadCreation", getFieldValue("Lead Creation"));
                leadHistory.put("modifiedTime", getFieldValue("Modified Time"));
                leadHistory.put("lastViewed", getFieldValue("Last Viewed"));
                json.put("leadHistory", leadHistory);

                Log.d("Lead_JSON", json.toString());
                callAddLeads(json.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void callAddLeads(String criteriaJson) {
        ProgressUtils.showProgressDialog(AddLeadsActivity.this);
        ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
        String token = "Bearer " + authToken; // Replace with your actual method to get token

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                criteriaJson
        );
        Call<String> call = apiInterface.addLeads(token,requestBody);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                ProgressUtils.hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(mContext,response.body(),Toast.LENGTH_SHORT).show();

                    Log.d("RESPONSE", "Success! Code: " + response.code());
                    Log.d("BODY", "Message: " + response.body());
//                    Intent resultIntent = new Intent();
//                    resultIntent.putExtra("templateID", templateID);
//                    setResult(Activity.RESULT_OK, resultIntent);
//                    finish();
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

    private String getFieldValue(String label) {
        for (FieldEntry entry : fieldEntries) {
            if (entry.label.equalsIgnoreCase(label)) {
                return entry.inputField.getText().toString().trim();
            }
        }
        return "";
    }


}
class FieldEntry {
    String label;
    boolean isRequired;
    EditText inputField;
    public Field field;

    FieldEntry(String label, boolean isRequired, EditText inputField,Field field) {
        this.label = label;
        this.isRequired = isRequired;
        this.inputField = inputField;
        this.field = field;

    }
}

