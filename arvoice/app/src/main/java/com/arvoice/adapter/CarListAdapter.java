package com.arvoice.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arvoice.model.CarListModel;

import java.util.List;

public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.CarViewHolder> {

    private final List<CarListModel.CarItem> carList;
    private final OnCarClickListener listener;

    public interface OnCarClickListener {
        void onCarSelected(CarListModel.CarItem item);
    }

    public CarListAdapter(List<CarListModel.CarItem> carList, OnCarClickListener listener) {
        this.carList = carList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        CarListModel.CarItem item = carList.get(position);
        holder.textView.setText(item.getCarName());
        holder.itemView.setOnClickListener(v -> listener.onCarSelected(item));
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
