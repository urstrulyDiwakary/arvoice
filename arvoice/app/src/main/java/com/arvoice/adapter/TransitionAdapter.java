package com.arvoice.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arvoice.R;
import com.arvoice.model.TransitionModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransitionAdapter extends RecyclerView.Adapter<TransitionAdapter.TaskViewHolder> {
    private Context mContext;
    private List<TransitionModel.TaskItem> taskList;

    private OnCardClickListener onCardClick; // Callback interface for amount change
    public interface OnCardClickListener {
        void onCardClick(String image);
    }
    public TransitionAdapter(Context mContext,List<TransitionModel.TaskItem> taskList, OnCardClickListener onCardClick) {
        this.mContext = mContext;
        this.taskList = taskList;
        this.onCardClick = onCardClick;
    }

    public void updateList(List<TransitionModel.TaskItem> newList) {
        this.taskList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transtion, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TransitionModel.TaskItem task = taskList.get(position);
        holder.tvName.setText(task.getTaskName());
        holder.tvStatus.setText(task.getStatus());
        holder.tvAssignedTo.setText(task.getAssignedTo());
        if (task.getCar()!=null && !task.getCar().trim().isEmpty()){
            holder.tvCar.setText(task.getCar());
        }if (task.getComments()!=null && !task.getComments().trim().isEmpty()){
            holder.tvComments.setText(task.getComments());
        }if (task.getCreatedTime()!=null && !task.getCreatedTime().trim().isEmpty()){
            holder.tvtime.setText(formatDueDate(task.getCreatedTime()));
        }if (task.getSiteVisitImage()!=null && !task.getSiteVisitImage().trim().isEmpty()){
            holder.tvSiteImage.setText("");
            holder.llImage.setVisibility(View.VISIBLE);
            try {
                byte[] decodedBytes = Base64.decode(task.getSiteVisitImage(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.ivImage.setImageBitmap(decodedBitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        holder.btnDown.setOnClickListener(view -> onCardClick.onCardClick(task.getSiteVisitImage()));
    }


    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAssignedTo,tvCar,tvComments,tvtime,tvSiteImage, tvStatus;
        LinearLayout llImage;
        ImageView ivImage;
        Button btnDown;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAssignedTo = itemView.findViewById(R.id.tvAssignedTo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCar = itemView.findViewById(R.id.tvCar);
            tvComments = itemView.findViewById(R.id.tvComments);
            tvtime = itemView.findViewById(R.id.tvtime);
            tvSiteImage = itemView.findViewById(R.id.tvSiteImage);
            llImage = itemView.findViewById(R.id.llImage);
            ivImage = itemView.findViewById(R.id.ivImage);
            btnDown = itemView.findViewById(R.id.btnDown);
        }
    }
    public static String formatDueDate(String dueDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dueDate);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Invalid date";
    }
}
