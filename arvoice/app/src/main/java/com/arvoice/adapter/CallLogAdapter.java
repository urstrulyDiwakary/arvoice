package com.arvoice.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arvoice.R;
import com.arvoice.model.CallLogItem;

import java.util.ArrayList;
import java.util.List;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    private List<CallLogItem> callLogList;
    private List<CallLogItem> filteredList; // Add filteredList variable

    public interface ItemClick{
        void onItemClick(String number);
    }
    public interface InfoClick{
        void onInfoClick(String number);
    }
    public ItemClick itemClick;
    public InfoClick onInfoClick;


    public CallLogAdapter(List<CallLogItem> callLogList, ItemClick itemClick,InfoClick infoClick) {
        this.callLogList = callLogList;
        this.filteredList = new ArrayList<>(callLogList); // Initialize filteredList with original data
        this.itemClick = itemClick;
        this.onInfoClick = infoClick;
    }

    public void updateFullData(List<CallLogItem> fullList) {
        this.callLogList = new ArrayList<>(fullList);
        this.filteredList = new ArrayList<>(fullList);
        notifyDataSetChanged();
    }
    public void filterList(String searchText) {
        filteredList.clear();
        if (searchText.isEmpty()) {
            filteredList.addAll(callLogList); // If search text is empty, show all items
        } else {
            for (CallLogItem leadListModel : callLogList) {
                String name = leadListModel.contactName.toLowerCase();
                String number = leadListModel.contactPhoneNo != null ? leadListModel.contactPhoneNo.toLowerCase() : "";

                if (name.contains(searchText.toLowerCase()) || number.contains(searchText.toLowerCase())) {
                    filteredList.add(leadListModel);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of dataset change
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconCallType;
        TextView textName, textNumber, textTime, textSim; // ✅ Added textSim
        LinearLayout llMain;

        public ViewHolder(View view) {
            super(view);
            iconCallType = view.findViewById(R.id.iconCallType);
            textName = view.findViewById(R.id.textName);
            textNumber = view.findViewById(R.id.textNumber);
            textTime = view.findViewById(R.id.textTime);
            textSim = view.findViewById(R.id.textSim); // ✅ Bind textSim
            llMain = view.findViewById(R.id.llMain); // ✅ Bind textSim
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_call_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallLogItem item = filteredList.get(position);
        holder.textName.setText(item != null ? item.contactName : "Unknown");
        holder.textNumber.setText(item.contactPhoneNo);
        holder.textTime.setText(item.callDuration+" sec");
        holder.textSim.setText(item.simInfo); // ✅ Set SIM info

        switch (item.callType) {
            case "Incoming":
                holder.iconCallType.setImageResource(R.drawable.incoming);
                break;
            case "Outgoing":
                holder.iconCallType.setImageResource(R.drawable.outgoing);
                break;
            case "Missed":
                holder.iconCallType.setImageResource(R.drawable.missed);
                break;
        }
        holder.llMain.setOnClickListener(view -> itemClick.onItemClick(item.contactPhoneNo));
        holder.textTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInfoClick.onInfoClick(item.contactPhoneNo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }
}
