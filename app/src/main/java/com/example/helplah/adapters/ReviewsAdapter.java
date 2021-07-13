package com.example.helplah.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.models.ProfilePictureHandler;
import com.example.helplah.models.Review;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * A recycler view adapter that fills the reviews page with a particular listing's review.
 */
public class ReviewsAdapter extends FirestorePagingAdapter<Review, ReviewsAdapter.ViewHolder> {

    // The context in which this adapter is used.
    private Context context;

    /**
     * Construct a new Reviews adapter.
     * @param options A firestore paging options that contains the query and paging configuration.
     */
    public ReviewsAdapter(@NonNull FirestorePagingOptions<Review> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull  Review model) {
        DocumentSnapshot snapshot = getItem(position);
        assert snapshot != null;
        holder.bind(Objects.requireNonNull(snapshot.toObject(Review.class)));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(this.context);
        return new ViewHolder(inflater.inflate(R.layout.review_list_item, parent, false));
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView reviewDate;
        private final MaterialRatingBar reviewRatingBar;
        private final TextView reviewText;
        private final TextView reviewUsername;
        private final TextView reviewService;
        private final MaterialCardView cardView;
        private final TextView replyTitle;
        private final TextView replyText;
        private final ImageView reviewerPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.reviewDate = itemView.findViewById(R.id.reviewDate);
            this.reviewRatingBar = itemView.findViewById(R.id.reviewRatingBar);
            this.reviewText = itemView.findViewById(R.id.reviewText);
            this.reviewUsername = itemView.findViewById(R.id.reviewUserName);
            this.reviewService = itemView.findViewById(R.id.reviewService);
            this.cardView = itemView.findViewById(R.id.reviewReplyCardView);
            this.replyTitle = itemView.findViewById(R.id.reviewRepliedTitle);
            this.replyText = itemView.findViewById(R.id.reviewReplyText);
            this.reviewerPicture = itemView.findViewById(R.id.reviewProfilePicture);
        }

        @SuppressLint("SetTextI18n")
        public void bind(final Review review) {
            this.reviewRatingBar.setRating(review.getScore());
            this.reviewText.setText(review.getReviewText());
            this.reviewUsername.setText(review.getUsername());
            this.reviewDate.setText(Review.getTimeAgo(review.getDateReviewed()));
            this.reviewService.setText("Service provided: " + review.getService());
            ProfilePictureHandler.setProfilePicture(this.reviewerPicture, review.getUserId(), context);

            if (review.getReply() != null) {
                this.cardView.setVisibility(View.VISIBLE);
                this.replyTitle.setText(review.getBusinessName() + " replied:");
                this.replyText.setText(review.getReply());
            }
        }

    }
}
