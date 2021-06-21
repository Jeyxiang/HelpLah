package com.example.helplah.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.models.Review;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.card.MaterialCardView;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewTabAdapter extends FirestorePagingAdapter<Review, ReviewTabAdapter.ViewHolder> {

    public interface OptionsListener {

        void optionClicked();

    }

    private static final String TAG = "Reviews viewpager tab adapter";

    private boolean business;
    private Context context;
    private OptionsListener listener;

    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */
    public ReviewTabAdapter(@NonNull FirestorePagingOptions<Review> options,
                            OptionsListener listener, boolean business) {
        super(options);
        this.listener = listener;
        this.business = business;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Review model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
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
        private final TextView optionsButton;

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
            this.optionsButton = itemView.findViewById(R.id.reviewOptionsButton);
        }

        @SuppressLint("SetTextI18n")
        public void bind(final Review review) {
            this.reviewRatingBar.setRating(review.getScore());
            this.reviewText.setText(review.getReviewText());
            if (business) {
                this.reviewUsername.setText(review.getUsername());
            } else {
                this.reviewUsername.setText("Reviewed: " + review.getBusinessName());
            }
            this.reviewDate.setText(Review.getTimeAgo(review.getDateReviewed()));
            this.reviewService.setText("Service provided: " + review.getService());

            if (review.getReply() != null) {
                this.cardView.setVisibility(View.VISIBLE);
                this.replyTitle.setText(review.getBusinessName() + " replied:");
                this.replyText.setText(review.getReply());
            }
            this.optionsButton.setVisibility(View.VISIBLE);
            configureOptions();
        }

        private void configureOptions() {
            this.optionsButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, optionsButton);
                if (business) {
                    popupMenu.inflate(R.menu.business_review_options);
                } else {
                    popupMenu.inflate(R.menu.user_review_options);
                }
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.replyReview || item.getItemId() == R.id.editReview) {
                        listener.optionClicked();
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });
        }
    }
}
