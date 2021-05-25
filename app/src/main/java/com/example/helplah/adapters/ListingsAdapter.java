package com.example.helplah.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.models.AvailabilityStatus;
import com.example.helplah.models.Listings;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

public class ListingsAdapter extends FirestorePagingAdapter<Listings, ListingsAdapter.ListingsViewHolder> {

    public ListingsAdapter(FirestorePagingOptions<Listings> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ListingsViewHolder holder, int position, @NonNull Listings model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public ListingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ListingsViewHolder(inflater.inflate(R.layout.list_item, parent, false));
    }

    public static class ListingsViewHolder extends RecyclerView.ViewHolder {

        private final TextView listingPrice;
        private final TextView listingAvailability;
        private final TextView listingName;
        private final TextView listingScore;
        private final TextView listingNumOfReviews;
        private final RatingBar listingRatingBar;


        public ListingsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.listingPrice = itemView.findViewById(R.id.listingPrice);
            this.listingAvailability = itemView.findViewById(R.id.listing_availability);
            this.listingName = itemView.findViewById(R.id.listingName);
            this.listingScore = itemView.findViewById(R.id.listingScore);
            this.listingNumOfReviews = itemView.findViewById(R.id.listingNumOfReviews);
            this.listingRatingBar = itemView.findViewById(R.id.listingRatingBar);
        }

        public void bind(Listings model) {
            this.listingPrice.setText("From $" + (int) model.getMinPrice());
            this.listingAvailability.setText(AvailabilityStatus.getAvailabilityText(model.getAvailability()));
            this.listingName.setText(model.getName());
            this.listingScore.setText(String.format("%.1f", model.getReviewScore()));
            this.listingNumOfReviews.setText("(" + model.getNumberOfReviews() + ")");
            this.listingRatingBar.setRating((float) model.getReviewScore());
        }
    }
}
