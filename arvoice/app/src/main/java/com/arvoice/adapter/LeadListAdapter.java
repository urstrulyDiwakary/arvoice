    package com.arvoice.adapter;

    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.cardview.widget.CardView;
    import androidx.recyclerview.widget.RecyclerView;

    import com.arvoice.R;
    import com.arvoice.model.LeadsListModel;

    import java.text.DecimalFormat;
    import java.util.ArrayList;
    import java.util.List;

    public class LeadListAdapter extends RecyclerView.Adapter<LeadListAdapter.MyViewHolder> {
        private Context context;
        private List<LeadsListModel.ContentItem> leadListModelArrayList;
        private List<LeadsListModel.ContentItem> filteredList; // Add filteredList variable

        private OnCardClickListener onCardClickListener; // Callback interface for amount change

        public LeadListAdapter(Context context, List<LeadsListModel.ContentItem> leadListModelArrayList, OnCardClickListener onCardClick) {
            this.context = context;
            this.leadListModelArrayList = leadListModelArrayList;
            this.filteredList = new ArrayList<>(leadListModelArrayList); // Initialize filteredList with original data
            this.onCardClickListener = onCardClick; // Initialize callback listener
        }

        public void updateFullData(List<LeadsListModel.ContentItem> fullList) {
            this.leadListModelArrayList = new ArrayList<>(fullList);
            this.filteredList = new ArrayList<>(fullList);
            notifyDataSetChanged();
        }

        public void filterList(String searchText) {
            filteredList.clear();
            if (searchText.isEmpty()) {
                filteredList.addAll(leadListModelArrayList); // If search text is empty, show all items
            } else {
                for (LeadsListModel.ContentItem leadListModel : leadListModelArrayList) {
                    String name = leadListModel.getContactName().toLowerCase();

                    if (name.contains(searchText.toLowerCase()) || name.contains(searchText.toLowerCase())) {
                        filteredList.add(leadListModel);
                    }
                }
            }
            notifyDataSetChanged(); // Notify adapter of dataset change
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            TextView leadIcon, leadName, leadStage, expectedRevenue, followUpDate, followUpNotes;
            CardView cvContent;

            public MyViewHolder(View view) {
                super(view);
                leadIcon = itemView.findViewById(R.id.lead_icon);
                leadName = itemView.findViewById(R.id.lead_name);
                leadStage = itemView.findViewById(R.id.lead_stage);
                expectedRevenue = itemView.findViewById(R.id.expected_revenue);
                followUpDate = itemView.findViewById(R.id.follow_up_date);
                followUpNotes = itemView.findViewById(R.id.follow_up_notes);
                cvContent = itemView.findViewById(R.id.cvContent);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lead_list_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            LeadsListModel.ContentItem lead = filteredList.get(position);
            Double expectedRevenue = lead.getExpectedRevenue();
            String revenue = "";
            if (expectedRevenue != null) {
                DecimalFormat format = new DecimalFormat("#");
                revenue = format.format(expectedRevenue);
                Log.d("Revenue", "Formatted: " + revenue);
            } else {
                Log.w("Revenue", "Expected revenue is null");
            }

            holder.leadIcon.setText(lead.getInitials());
            holder.leadName.setText(lead.getContactName());
            holder.leadStage.setText(lead.getLeadStage());
            holder.expectedRevenue.setText(revenue);
            holder.followUpDate.setText(lead.getLeadInfoExtnAttr().getNextFollowUpOn());
            holder.followUpNotes.setText(lead.getLeadInfoExtnAttr().getNextFollowUpOn());
            holder.cvContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCardClickListener.onCardClick(position);
                }
            });
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
