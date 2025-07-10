    package com.arvoice.adapter;

    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.cardview.widget.CardView;
    import androidx.recyclerview.widget.RecyclerView;

    import com.arvoice.R;
    import com.arvoice.model.UserModel;

    import java.util.ArrayList;
    import java.util.List;

    public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
        private Context context;
        private List<UserModel> userModelList;
        private List<UserModel> filteredList; // Add filteredList variable

        private OnCardClickListener onCardClickListener; // Callback interface for amount change

        public UserListAdapter(Context context, List<UserModel> taskListModelArrayList, OnCardClickListener onCardClick) {
            this.context = context;
            this.userModelList = taskListModelArrayList;
            this.filteredList = new ArrayList<>(taskListModelArrayList); // Initialize filteredList with original data
            this.onCardClickListener = onCardClick; // Initialize callback listener
        }
        public UserModel getFilteredItem(int position) {
            return filteredList.get(position);
        }

        public void updateFullData(List<UserModel> fullList) {
            this.userModelList = new ArrayList<>(fullList);
            this.filteredList = new ArrayList<>(fullList);
            notifyDataSetChanged();
        }
        public void filterList(String searchText) {
            filteredList.clear();
            if (searchText.isEmpty()) {
                filteredList.addAll(userModelList); // If search text is empty, show all items
            } else {
                for (UserModel leadListModel : userModelList) {
                    String name = leadListModel.getUsername().toLowerCase();

                    if (name.contains(searchText.toLowerCase()) || name.contains(searchText.toLowerCase())) {
                        filteredList.add(leadListModel);
                    }
                }
            }
            notifyDataSetChanged(); // Notify adapter of dataset change
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvEmail, tvMobile,tvProfile,tvReporting;
            CardView cvContent;

            public MyViewHolder(View view) {
                super(view);
                tvName = itemView.findViewById(R.id.tvName);
                tvEmail = itemView.findViewById(R.id.tvEmail);
                tvMobile = itemView.findViewById(R.id.tvMobile);
                tvProfile = itemView.findViewById(R.id.tvProfile);
                tvReporting = itemView.findViewById(R.id.tvReporting);

                cvContent = itemView.findViewById(R.id.cvContent);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            UserModel lead = filteredList.get(position);
            holder.tvName.setText(lead.getUsername());
            holder.tvEmail.setText(lead.getEmail());
            holder.tvMobile.setText(lead.getMobile());
            holder.tvProfile.setText(lead.getProfile());
            if (lead.getReportingTo()!=null && !lead.getReportingTo().trim().isEmpty()){
                holder.tvReporting.setText(lead.getReportingTo());
            }
            holder.cvContent.setOnClickListener(view -> onCardClickListener.onCardClick(position));
        }


            @Override
        public int getItemCount() {
            return filteredList.size(); // Return size of filtered list
        }

        // Callback interface for passing the amount
        public interface OnCardClickListener {
            void onCardClick(int position);
        }



    }
