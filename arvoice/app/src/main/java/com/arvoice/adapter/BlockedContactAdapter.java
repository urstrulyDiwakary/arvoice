package com.arvoice.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arvoice.R;
import com.arvoice.model.BlockedContact;

import java.util.List;

public class BlockedContactAdapter extends RecyclerView.Adapter<BlockedContactAdapter.ViewHolder> {

    public interface OnDeleteListener {
        void onDelete(BlockedContact contact);
    }

    private List<BlockedContact> contactList;
    private OnDeleteListener listener;

    public BlockedContactAdapter(List<BlockedContact> contactList, OnDeleteListener listener) {
        this.contactList = contactList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;
        ImageButton delete;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            number = view.findViewById(R.id.number);
            delete = view.findViewById(R.id.btn_delete);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blocked_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BlockedContact contact = contactList.get(position);
        holder.name.setText(contact.name);
        holder.number.setText(contact.number);
        holder.delete.setOnClickListener(v -> {
            listener.onDelete(contact);
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}