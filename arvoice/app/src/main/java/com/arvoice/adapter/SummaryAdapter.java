package com.arvoice.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.arvoice.R;
import com.arvoice.model.SummaryItem;

import java.util.ArrayList;
import java.util.List;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder> {

    private List<SummaryItem> summaryItems = new ArrayList<>();
    private List<SummaryItem> originalList = new ArrayList<>();
    private final Context context;
    private final OnSummaryClickListener listener;

    public interface OnSummaryClickListener {
        void onSummaryClick(int templateID);
    }

    public SummaryAdapter(Context context, List<SummaryItem> summaryItems, OnSummaryClickListener onSummaryClickListener) {
        this.context = context;
        this.listener = onSummaryClickListener;
        updateFullData(summaryItems);
    }

    @NonNull
    @Override
    public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_summary_card, parent, false);
        return new SummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SummaryItem item = summaryItems.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());

        holder.tvSubtitle.setVisibility(item.getSubtitle().isEmpty() ? View.GONE : View.VISIBLE);
        holder.tvValue.setText(item.getValue());

        try {
            holder.tvValue.setTextColor(Color.parseColor(item.getColorCode()));
        } catch (IllegalArgumentException e) {
            holder.tvValue.setTextColor(Color.BLACK); // fallback
        }

        holder.cvCard.setOnClickListener(view -> listener.onSummaryClick(item.getTemplateId()));
    }

    @Override
    public int getItemCount() {
        return summaryItems.size();
    }

    // Used when filtering (search)
    public void updateData(List<SummaryItem> filteredList) {
        this.summaryItems = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }

    // Used after new API data is fetched
    public void updateFullData(List<SummaryItem> fullList) {
        this.originalList = new ArrayList<>(fullList);
        this.summaryItems = new ArrayList<>(fullList);
        notifyDataSetChanged();
    }

    public List<SummaryItem> getOriginalList() {
        return originalList;
    }

    static class SummaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvValue;
        CardView cvCard;
        LinearLayout llContent;

        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvValue = itemView.findViewById(R.id.tvValue);
            cvCard = itemView.findViewById(R.id.cvCard);
            llContent = itemView.findViewById(R.id.llContent);
        }
    }
}
