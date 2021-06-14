package com.example.helplah.viewmodel.consumer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.ReviewsAdapter;
import com.example.helplah.models.Listings;
import com.example.helplah.models.Review;
import com.example.helplah.models.ReviewQuery;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ViewReviewsFragment extends Fragment {

    public static final String  TAG = "View review fragment";

    public static class ReviewsViewModel extends ViewModel {

        private Query query;
        private int toolbarSelectedPosition;

        public int getToolbarSelectedPosition() {
            return toolbarSelectedPosition;
        }

        public void setToolbarSelectedPosition(int toolbarSelectedPosition) {
            this.toolbarSelectedPosition = toolbarSelectedPosition;
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
    private TextView reviewScore;
    private MaterialToolbar toolbar;
    private FirestorePagingOptions<Review> options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(ReviewsViewModel.class);
        this.listingId = getArguments().getString("id");

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
        this.toolbar = this.rootView.findViewById(R.id.reviewsToolbar);
        this.reviewScore = this.rootView.findViewById(R.id.reviewScore);

        this.reviewScore.setText(String.format("%.1f",
                getArguments().getDouble(Listings.FIELD_REVIEW_SCORE)));

        getQuery();
        configureToolbar();

        return this.rootView;
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

    private void filterOptionClicked() {

        String[] sortOptions = {"Highest First", "Lowest First", "Most Recent"};

        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Sort by?")
                .setSingleChoiceItems(sortOptions, this.viewModel.getToolbarSelectedPosition(),
                        ((dialog, which) -> {
                            if (which == 0) { // Sort by Highest First
                                this.viewModel.setToolbarSelectedPosition(0);
                                changeQuery(true, false);
                                dialog.dismiss();
                            } else if (which == 1) { // sort by Lowest first
                                this.viewModel.setToolbarSelectedPosition(1);
                                changeQuery(true, true);
                                dialog.dismiss();
                            } else { // Sort by most recent first
                                this.viewModel.setToolbarSelectedPosition(2);
                                changeQuery(false, false);
                                dialog.dismiss();
                            }
                        })).show();
    }

    private void changeQuery(boolean score, boolean ascending) {
        ReviewQuery query = new ReviewQuery(FirebaseFirestore.getInstance(), this.listingId);

        if (score && ascending) {
            query.setSortBy(Review.FIELD_SCORE, true);
        } else if (score) {
            query.setSortBy(Review.FIELD_SCORE, false);
        } else {
            query.setSortBy(Review.FIELD_DATE_REVIEWED, false);
        }

        this.viewModel.setQuery(query.createQuery());
        this.options = new FirestorePagingOptions.Builder<Review>()
                .setLifecycleOwner(this)
                .setQuery(this.viewModel.getQuery(), this.rvConfig, Review.class)
                .build();
        this.rvAdapter.updateOptions(this.options);
    }
    private void configureToolbar() {
        this.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.topBarFilter) {
                filterOptionClicked();
                return true;
            }
            return false;
        });

        this.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }
}