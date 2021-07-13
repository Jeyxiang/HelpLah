package com.example.helplah.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.models.Listings;
import com.example.helplah.models.ProfilePictureHandler;
import com.example.helplah.models.Review;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * A recycler view adapter that fills the reviews tab in the account page with reviews. If the
 * user is a business, then the adapter fills the page with the business's reviews. If the user is
 * a consumer, then the adapter fills the page with the reviews written by the user.
 *
 * In order for the adapter to differentiate between a business and consumer user, a boolean is
 * passed into the constructor depending on whether it is called from the business or consumer
 * interface.
 */
public class ReviewTabAdapter extends FirestorePagingAdapter<Review, ReviewTabAdapter.ViewHolder> {

    /**
     * A listener interface that listens on when the option's tab on a reviews is clicked.
     * Clicking the option's tab will open an overflow menu with different options.
     *
     * The options displayed is configured in the ConfigureOptions method in the viewHolder class
     * below.
     */
    public interface OptionsListener {

        void optionClicked(View v, Review review);

    }

    private static final String TAG = "Reviews viewpager tab adapter";

    private boolean business;
    private Context context;
    private OptionsListener listener;

    /**
     * Construct a new ReviewTabAdapter.
     * @param options A firestore paging options that contains the query and paging configuration.
     * @param listener A listener to listen on clicks on each review's option tab.
     * @param business A boolean to indicate of the adapter is being used by a business or consumer
     *                 user.
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
        return new ViewHolder(inflater.inflate(R.layout.review_list_item, parent, false),
                parent.getContext());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        private final TextView reviewDate;
        private final MaterialRatingBar reviewRatingBar;
        private final TextView reviewText;
        private final TextView reviewUsername;
        private final TextView reviewService;
        private final MaterialCardView cardView;
        private final TextView replyTitle;
        private final TextView replyText;
        private final TextView optionsButton;
        private final ImageView image;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            this.reviewDate = itemView.findViewById(R.id.reviewDate);
            this.reviewRatingBar = itemView.findViewById(R.id.reviewRatingBar);
            this.reviewText = itemView.findViewById(R.id.reviewText);
            this.reviewUsername = itemView.findViewById(R.id.reviewUserName);
            this.reviewService = itemView.findViewById(R.id.reviewService);
            this.cardView = itemView.findViewById(R.id.reviewReplyCardView);
            this.replyTitle = itemView.findViewById(R.id.reviewRepliedTitle);
            this.replyText = itemView.findViewById(R.id.reviewReplyText);
            this.optionsButton = itemView.findViewById(R.id.reviewOptionsButton);
            this.image = itemView.findViewById(R.id.reviewProfilePicture);
        }

        @SuppressLint("SetTextI18n")
        public void bind(final Review review) {
            this.reviewRatingBar.setRating(review.getScore());
            this.reviewText.setText(review.getReviewText());
            if (business) {
                this.reviewUsername.setText(review.getUsername());
                ProfilePictureHandler.setProfilePicture(this.image, review.getUserId(), this.context);
            } else {
                this.reviewUsername.setText("Reviewed: " + review.getBusinessName());
                ProfilePictureHandler.setProfilePicture(this.image, review.getBusinessId(), this.context);
                this.image.setOnClickListener(v -> goToBusinessListing(review));
            }
            this.reviewDate.setText(Review.getTimeAgo(review.getDateReviewed()));
            this.reviewService.setText("Service provided: " + review.getService());

            if (review.getReply() != null) {
                this.cardView.setVisibility(View.VISIBLE);
                this.replyTitle.setText(review.getBusinessName() + " replied:");
                this.replyText.setText(review.getReply());
            } else {
                this.cardView.setVisibility(View.GONE);
            }
            this.optionsButton.setVisibility(View.VISIBLE);
            configureOptions(review);
        }


        /**
         * Configure the review option overflow tab of the review adapter item.
         * @param review The individual reviews in the adapter.
         */
        private void configureOptions(Review review) {
            this.optionsButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, optionsButton);
                if (business) {
                    popupMenu.inflate(R.menu.business_review_options);
                } else {
                    popupMenu.inflate(R.menu.user_review_options);
                }
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.replyReview || item.getItemId() == R.id.editReview) {
                        listener.optionClicked(v, review);
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });
        }

        /**
         * Go to the business listing of a review. It is activated when a user clicks the profile
         * picture of a review item. This is only applicable to the consumer user interface.
         * @param review The review whose profile picture is clicked.
         */
        private void goToBusinessListing(Review review) {
            String businessId = review.getBusinessId();
            CollectionReference listingsDb = FirebaseFirestore.getInstance().collection(Listings.DATABASE_COLLECTION);
            Bundle bundle = new Bundle();

            listingsDb.document(businessId).get().addOnSuccessListener(snapshot -> {
                Listings listing = snapshot.toObject(Listings.class);
                bundle.putParcelable("listing", listing);
                bundle.putString("id", businessId);
                bundle.putString("category", review.getService());
                Navigation.findNavController(itemView)
                        .navigate(R.id.action_accountFragment_to_listingDescription, bundle);
            });
        }
    }
}
