package com.example.helplah.adapters;

import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.DocumentSnapshot;

public class ListingsAdapter extends FirestorePagingAdapter<Listings, ListingsAdapter.ListingsViewHolder> {

    public interface onListingSelectedListener {

        void onListingClicked(DocumentSnapshot listing, View v);

    }

    private onListingSelectedListener mListener;

    public ListingsAdapter(FirestorePagingOptions<Listings> options, onListingSelectedListener listener) {
        super(options);
        this.mListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ListingsViewHolder holder, int position, @NonNull Listings model) {
        DocumentSnapshot snapshot = getItem(position);
        holder.bind(snapshot, this.mListener);
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

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        public void bind(final DocumentSnapshot snapshot, final onListingSelectedListener listener) {

            Listings listing = snapshot.toObject(Listings.class);

            this.listingPrice.setText("From $" + (int) listing.getMinPrice());
            this.listingAvailability.setText(AvailabilityStatus.getAvailabilityText(listing.getAvailability()));
            this.listingName.setText(listing.getName());
            this.listingScore.setText(String.format("%.1f", listing.getReviewScore()));
            this.listingNumOfReviews.setText(listing.getNumberOfReviews() + " reviews");
            this.listingRatingBar.setRating((float) listing.getReviewScore());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onListingClicked(snapshot, v);
                    }
                }
            });
        }
    }
}
