package com.arvoice.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arvoice.R;
import com.arvoice.model.LeadsFilter;

import java.util.ArrayList;
import java.util.List;

public class LeadOptionAdapter extends RecyclerView.Adapter<LeadOptionAdapter.ViewHolder> {

    private List<LeadsFilter.TemplateItem> options;
    private OnItemClickListener listener;
    private int selectedIndex = 0; // default selected

    public interface OnItemClickListener {
        void onItemClick(String item,String criteria);
    }

    public LeadOptionAdapter(List<LeadsFilter.TemplateItem> options, OnItemClickListener listener) {
        this.options = options;
        this.listener = listener;
    }

    public void updateFullData(List<LeadsFilter.TemplateItem> fullList) {
        this.options = new ArrayList<>(fullList);
        notifyDataSetChanged();
    }
    public void setSelectedIndex(int index) {
        int oldIndex = selectedIndex;
        selectedIndex = index;
        notifyItemChanged(oldIndex);
        notifyItemChanged(selectedIndex);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lead_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        LeadsFilter.TemplateItem item = options.get(position);
        holder.tvOption.setText(item.getName());

        // Show checkmark for selected item
        holder.ivCheck.setVisibility(position == selectedIndex ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            int oldIndex = selectedIndex;
            selectedIndex = position;
            notifyItemChanged(oldIndex);
            notifyItemChanged(selectedIndex);
            listener.onItemClick(item.getName(),item.getSearchCriteriaList());
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOption;
        ImageView ivCheck;
        public ViewHolder(View view) {
            super(view);
            tvOption = view.findViewById(R.id.tvOption);
            ivCheck = view.findViewById(R.id.ivCheck);
        }
    }
}
