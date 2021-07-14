package com.example.helplah.viewmodel.consumer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.ReviewsAdapter;
import com.example.helplah.models.Listings;
import com.example.helplah.models.ProfilePictureHandler;
import com.example.helplah.models.Review;
import com.example.helplah.models.ReviewQuery;
import com.example.helplah.models.Services;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Shows all the reviews of a particular listing.
 *
 * This fragment requires the following arguments in the bundle when navigating to it:
 * id - String: The id of the listing whose reviews are to be displayed.
 * Service - String: This is an optional argument. It is only used when a consumer user searches for a
 * listing using the different categories tabs in the home page. This argument will allow the user to
 * filter the reviews to only show the reviews provided for a certain service. for example, if a business
 * provides multiple services and the user is searching for plumbers, then "plumber is passed as an
 * argument to this fragment. The user can then filter to displays reviews where the business has
 * been hired for a plumber job.
 * reviewScore - double: The average review score of the listing.
 */
public class ViewReviewsFragment extends Fragment {

    public static final String  TAG = "View review fragment";

    public static class ReviewsViewModel extends ViewModel {

        private Query query;
        private int ratingFilter = 0;
        private boolean allServices = true;

        public int getRatingFilter() {
            return ratingFilter;
        }

        public void setRatingFilter(int ratingFilter) {
            this.ratingFilter = ratingFilter;
        }

        public Query getQuery() {
            return query;
        }

        public void setQuery(Query query) {
            this.query = query;
        }
    }

    private ReviewsViewModel viewModel;
    private View rootView;
    private RecyclerView rvReviews;
    private ReviewsAdapter rvAdapter;
    private PagedList.Config rvConfig;
    private String listingId;
    private String category;
    private FirestorePagingOptions<Review> options;
    private ArrayList<CardView> allRatingsFilters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(ReviewsViewModel.class);
        this.listingId = getArguments().getString("id");
        this.category = getArguments().getString(Services.SERVICE);
        Log.d(TAG, "onCreate: " + this.category);

        ReviewQuery reviewQuery = new ReviewQuery(FirebaseFirestore.getInstance(), this.listingId);
        reviewQuery.setSortBy(Review.FIELD_DATE_REVIEWED, false);

        if (this.viewModel.getQuery() != null) {
            configureFireStore(this.viewModel.getQuery());
            return;
        }

        this.viewModel.setQuery(reviewQuery.createQuery());
        configureFireStore(this.viewModel.getQuery());
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.fragment_view_reviews, container, false);
        this.rvReviews = this.rootView.findViewById(R.id.reviewsRecyclerView);
        MaterialToolbar toolbar = this.rootView.findViewById(R.id.reviewsToolbar);
        TextView reviewScore = this.rootView.findViewById(R.id.reviewScore);
        CircleImageView imageView = this.rootView.findViewById(R.id.reviewProfilePicture);

        ProfilePictureHandler.setProfilePicture(imageView, this.listingId, requireActivity());
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        reviewScore.setText(String.format("%.1f",
                getArguments().getDouble(Listings.FIELD_REVIEW_SCORE)));

        getQuery();
        bindFilters();

        return this.rootView;
    }

    @SuppressLint("SetTextI18n")
    private void bindFilters() {
        CardView all = this.rootView.findViewById(R.id.reviewSelectedAll);
        CardView fiveStar = this.rootView.findViewById(R.id.reviewSelected5Stars);
        CardView fourStar = this.rootView.findViewById(R.id.reviewSelected4Stars);
        CardView threeStar = this.rootView.findViewById(R.id.reviewSelected3Stars);
        CardView twoStar = this.rootView.findViewById(R.id.reviewSelected2Stars);
        CardView oneStar = this.rootView.findViewById(R.id.reviewSelected1Stars);
        allRatingsFilters = new ArrayList<>(Arrays.asList(all, oneStar, twoStar, threeStar,
                fourStar, fiveStar));
        for (int i = 0; i < this.allRatingsFilters.size(); i++) {
            final int x = i;
            this.allRatingsFilters.get(i).setOnClickListener(v -> ratingFilterSelected(x));
            changeQuery();
        }

        CardView services = this.rootView.findViewById(R.id.reviewCategoryFilters);
        if (this.category == null) {
            services.setVisibility(View.GONE);
        }

        TextView serviceText = this.rootView.findViewById(R.id.reviewCategoryText);
        serviceText.setText("Show results for " + this.category + " only");
        services.setOnClickListener(v -> serviceFilterSelected(services));

        this.allRatingsFilters.get(this.viewModel.getRatingFilter())
                .setCardBackgroundColor(Color.parseColor("#800CC96E"));
        services.setCardBackgroundColor(this.viewModel.allServices ? Color.parseColor("#222222")
                : Color.parseColor("#800CC96E"));
    }

    private void getQuery() {
        this.rvAdapter = new ReviewsAdapter(this.options);
        this.rvAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        this.rvReviews.setLayoutManager(new LinearLayoutManager(requireActivity()));

        this.rvReviews.setAdapter(this.rvAdapter);
    }

    private void configureFireStore(Query query) {
        this.rvConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(30)
                .setPageSize(10)
                .setPrefetchDistance(10)
                .build();

        this.options = new FirestorePagingOptions.Builder<Review>()
                .setLifecycleOwner(this)
                .setQuery(query, this.rvConfig, Review.class)
                .build();
    }

    private void ratingFilterSelected(int rating) {
        for (CardView card : this.allRatingsFilters) {
            card.setCardBackgroundColor(Color.parseColor("#222222"));
        }
        this.allRatingsFilters.get(rating).setCardBackgroundColor(Color.parseColor("#800CC96E"));
        this.viewModel.setRatingFilter(rating);
        changeQuery();
    }

    private void serviceFilterSelected(CardView card) {
        if (this.viewModel.allServices) {
            this.viewModel.allServices = false;
            card.setCardBackgroundColor(Color.parseColor("#800CC96E"));
        } else {
            this.viewModel.allServices = true;
            card.setCardBackgroundColor(Color.parseColor("#222222"));
        }
        changeQuery();
    }

    private void changeQuery() {
        ReviewQuery query = new ReviewQuery(FirebaseFirestore.getInstance(), this.listingId);

        query.setRating(this.viewModel.getRatingFilter());
        query.setSortBy(Review.FIELD_DATE_REVIEWED, false);
        if (!this.viewModel.allServices) {
            query.setService(this.category);
        }

        this.viewModel.setQuery(query.createQuery());
        this.options = new FirestorePagingOptions.Builder<Review>()
                .setLifecycleOwner(this)
                .setQuery(this.viewModel.getQuery(), this.rvConfig, Review.class)
                .build();
        this.rvAdapter.updateOptions(this.options);
    }
}