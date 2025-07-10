package com.arvoice.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arvoice.R;
import com.arvoice.model.CallLogModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CallDetailsAdapter extends RecyclerView.Adapter<CallDetailsAdapter.ViewHolder> {

    private List<CallLogModel> callLogList;

    public interface ItemClick{
        void onItemClick(String number);
    }
    public ItemClick itemClick;


    public CallDetailsAdapter(List<CallLogModel> callLogList, ItemClick itemClick) {
        this.callLogList = callLogList;
        this.itemClick = itemClick;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDateTime, tvDuration;
        ImageView iconCallType;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            iconCallType = itemView.findViewById(R.id.iconCallType); // ✅ Bind textSim

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallLogModel log = callLogList.get(position);
        holder.tvType.setText(log.type);
        holder.tvDateTime.setText(new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.getDefault()).format(log.date));
        holder.tvDuration.setText(log.type.equals("Answered") ? log.duration + " sec" : "");
        if (log.simInfo.equalsIgnoreCase("SIM 1")) {
            holder.iconCallType.setImageResource(R.drawable.sim1); // your SIM 1 icon
        } else if (log.simInfo.equalsIgnoreCase("SIM 2")) {
            holder.iconCallType.setImageResource(R.drawable.sim2); // your SIM 2 icon
        } else {
            holder.iconCallType.setImageResource(R.drawable.simcard_1); // fallback/default icon
        }

    }

    @Override
    public int getItemCount() {
        return callLogList.size();
    }
}
