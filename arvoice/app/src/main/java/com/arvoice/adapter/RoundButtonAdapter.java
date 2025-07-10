package com.arvoice.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arvoice.R;

import java.util.List;

public class RoundButtonAdapter extends RecyclerView.Adapter<RoundButtonAdapter.RoundButtonViewHolder> {

    private List<String> items;
    private Context context;
    private int selectedPosition = 0; // ✅ Set default selection to 0 (first item)
    public interface OnItemClickListener {
        void onItemClick(String selected);
    }
    private OnItemClickListener listener;

    public RoundButtonAdapter(Context context, List<String> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoundButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_round_button, parent, false);
        return new RoundButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoundButtonViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String item = items.get(position);
        holder.btnItem.setText(item);

        // Set selected/unselected background and text color
        if (position == selectedPosition) {
            holder.btnItem.setBackgroundResource(R.drawable.bg_selected);
            holder.btnItem.setTextColor(Color.WHITE);
        } else {
            holder.btnItem.setBackgroundResource(R.drawable.bg_unselected);
            holder.btnItem.setTextColor(Color.BLACK);
        }

        // Handle click
        holder.btnItem.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            listener.onItemClick(item); // Call callback to update summary
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class RoundButtonViewHolder extends RecyclerView.ViewHolder {
        TextView btnItem;

        public RoundButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            btnItem = itemView.findViewById(R.id.btnItem);
        }
    }
}
