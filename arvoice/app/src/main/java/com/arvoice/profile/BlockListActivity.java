package com.arvoice.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arvoice.adapter.BlockedContactAdapter;
import com.arvoice.databinding.ActivityBlockListBinding;
import com.arvoice.model.BlockedContact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BlockListActivity extends AppCompatActivity {
    private ActivityBlockListBinding binding;
    private SharedPreferences prefs;

    private static final int CONTACT_PICK = 101;

    private List<BlockedContact> blockList = new ArrayList<>();
    private BlockedContactAdapter adapter;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlockListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        setupToolbar();
        blockList = getBlockedContacts();

        adapter = new BlockedContactAdapter(blockList, contact -> {
            blockList.remove(contact);
            saveBlockedContacts(blockList);
            refreshList();
        });

        binding.recyclerBlocked.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerBlocked.setAdapter(adapter);

        binding.btnAddContact.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, CONTACT_PICK);
        });

        binding.btnAddManual.setOnClickListener(v -> {
            showManualAddDialog();
        });

        refreshList();
    }

    private void setupToolbar() {
        binding.ivBack.setOnClickListener(view -> finish());
    }

    private void refreshList() {
        binding.emptyText.setVisibility(blockList.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void showManualAddDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter phone number");

        new AlertDialog.Builder(this)
                .setTitle("Block Number")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String number = input.getText().toString().trim();
                    if (!number.isEmpty()) {
                        blockList.add(new BlockedContact("Manual", number));
                        saveBlockedContacts(blockList);
                        refreshList();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveBlockedContacts(List<BlockedContact> list) {
        prefs.edit().putString("blocked_contacts", gson.toJson(list)).apply();
    }

    private List<BlockedContact> getBlockedContacts() {
        String json = prefs.getString("blocked_contacts", "[]");
        Type type = new TypeToken<List<BlockedContact>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTACT_PICK && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        .replaceAll("\\s+", "");
                blockList.add(new BlockedContact(name, number));
                saveBlockedContacts(blockList);
                refreshList();
                cursor.close();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}