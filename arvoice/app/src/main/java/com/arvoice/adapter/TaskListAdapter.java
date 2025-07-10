    package com.arvoice.adapter;

    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.cardview.widget.CardView;
    import androidx.recyclerview.widget.RecyclerView;

    import com.arvoice.R;
    import com.arvoice.model.TaskListModel;

    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;

    public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.MyViewHolder> {
        private Context context;
        private List<TaskListModel.TaskItem> taskItemList;
        private List<TaskListModel.TaskItem> filteredList; // Add filteredList variable

        private OnCardClickListener onCardClickListener; // Callback interface for amount change
        private OnEditClickListener onEditClickListener; // Callback interface for amount change

        public TaskListAdapter(Context context, List<TaskListModel.TaskItem> taskListModelArrayList, OnCardClickListener onCardClick,OnEditClickListener onEditClickListener) {
            this.context = context;
            this.taskItemList = taskListModelArrayList;
            this.filteredList = new ArrayList<>(taskListModelArrayList); // Initialize filteredList with original data
            this.onCardClickListener = onCardClick; // Initialize callback listener
            this.onEditClickListener = onEditClickListener; // Initialize callback listener
        }
        public void updateFullData(List<TaskListModel.TaskItem> fullList) {
            this.taskItemList = new ArrayList<>(fullList);
            this.filteredList = new ArrayList<>(fullList);
            notifyDataSetChanged();
        }
        public void filterList(String searchText) {
            filteredList.clear();
            if (searchText.isEmpty()) {
                filteredList.addAll(taskItemList); // If search text is empty, show all items
            } else {
                for (TaskListModel.TaskItem leadListModel : taskItemList) {
                    String name = leadListModel.taskName.toLowerCase();

                    if (name.contains(searchText.toLowerCase()) || name.contains(searchText.toLowerCase())) {
                        filteredList.add(leadListModel);
                    }
                }
            }
            notifyDataSetChanged(); // Notify adapter of dataset change
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            TextView taskName, tvLeadName,tvDueOn,tvComments,tvStatus,tvAssigned;
            CardView cvContent;
            Button btnMap;
            ImageView ivEdit;

            public MyViewHolder(View view) {
                super(view);
                taskName = itemView.findViewById(R.id.taskName);
                tvLeadName = itemView.findViewById(R.id.tvLeadName);
                tvDueOn = itemView.findViewById(R.id.tvDueOn);
                tvComments = itemView.findViewById(R.id.tvComments);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvAssigned = itemView.findViewById(R.id.tvAssigned);
                btnMap = itemView.findViewById(R.id.btnMap);
                ivEdit = itemView.findViewById(R.id.ivEdit);

                cvContent = itemView.findViewById(R.id.cvContent);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            TaskListModel.TaskItem lead = filteredList.get(position);
            holder.taskName.setText(lead.taskName);
            holder.tvLeadName.setText(lead.lead.contactName);
            holder.tvDueOn.setText(formatDueDate(lead.dueDate));
            holder.tvComments.setText(lead.comments);
            holder.tvStatus.setText(lead.status);
            if (lead.status.equalsIgnoreCase("CLOSED")){
                holder.ivEdit.setVisibility(View.GONE);
            }else {
                holder.ivEdit.setVisibility(View.VISIBLE);
            }
            holder.tvAssigned.setText(lead.assignedTo);
            holder.btnMap.setOnClickListener(view -> onCardClickListener.onCardClick(position));
            holder.ivEdit.setOnClickListener(view -> onEditClickListener.OnEditClickListener(position));
        }


            @Override
        public int getItemCount() {
            return filteredList.size(); // Return size of filtered list
        }

        // Callback interface for passing the amount
        public interface OnCardClickListener {
            void onCardClick(int position);
        }
        public interface OnEditClickListener {
            void OnEditClickListener(int position);
        }

        public static String formatDueDate(String dueDate) {
            if (dueDate == null || dueDate.trim().isEmpty()) {
                return "Invalid date";
            }

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
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
