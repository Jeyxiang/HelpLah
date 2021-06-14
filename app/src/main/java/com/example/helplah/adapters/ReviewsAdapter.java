package com.example.helplah.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.models.Review;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewsAdapter extends FirestorePagingAdapter<Review, ReviewsAdapter.viewHolder> {

    private Context context;

    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */
    public ReviewsAdapter(@NonNull FirestorePagingOptions<Review> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull  Review model) {
        DocumentSnapshot snapshot = getItem(position);
        holder.bind(snapshot.toObject(Review.class));
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(this.context);
        return new viewHolder(inflater.inflate(R.layout.review_list_item, parent, false));
    }



    public static class viewHolder extends RecyclerView.ViewHolder {

        private final TextView reviewDate;
        private final MaterialRatingBar reviewRatingBar;
        private final TextView reviewText;
        private final TextView reviewUsername;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            this.reviewDate = itemView.findViewById(R.id.reviewDate);
            this.reviewRatingBar = itemView.findViewById(R.id.reviewRatingBar);
            this.reviewText = itemView.findViewById(R.id.reviewText);
            this.reviewUsername = itemView.findViewById(R.id.reviewUserName);
        }

        public void bind(final Review review) {
            this.reviewRatingBar.setRating(review.getScore());
            this.reviewText.setText(review.getReviewText());
            this.reviewUsername.setText(review.getUsername());
            this.reviewDate.setText(Review.getTimeAgo(review.getDateReviewed()));
        }

    }
}
