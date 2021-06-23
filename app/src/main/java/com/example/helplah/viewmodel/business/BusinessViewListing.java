package com.example.helplah.viewmodel.business;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.CategoriesAdapter;
import com.example.helplah.adapters.DescriptionCategoryAdapter;
import com.example.helplah.adapters.ReviewsAdapter;
import com.example.helplah.models.AvailabilityStatus;
import com.example.helplah.models.Listings;
import com.example.helplah.models.Review;
import com.example.helplah.models.ReviewQuery;
import com.example.helplah.models.Services;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class BusinessViewListing extends Fragment implements CategoriesAdapter.onCategorySelected {

    private static final String TAG = "Business View Listing";

    private Listings listing;
    private String listingId;

    private View rootView;
    private RecyclerView rvServices;
    private RecyclerView rvReviews;
    private TextView reviewScore;
    private TextView numberOfReviews;
    private RatingBar ratingBar;
    private TextView description;
    private TextView minPrice;
    private TextView pricingNote;
    private TextView availability;
    private TextView website;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");

        this.listing = this.getArguments().getParcelable("listing");
        this.listingId = this.getArguments().getString("id");

        if (this.listing == null) {
            Toast.makeText(getActivity(), "An unexpected error occurred", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.rootView =  inflater.inflate(R.layout.business_view_listing_fragment, container, false);

        AppBarLayout appBarLayout = this.rootView.findViewById(R.id.descriptionAppBar);
        this.reviewScore = this.rootView.findViewById(R.id.descriptionScore);
        this.numberOfReviews = this.rootView.findViewById(R.id.descriptionNumOfReviews);
        this.ratingBar = this.rootView.findViewById(R.id.descriptionRatingBar);
        this.description = this.rootView.findViewById(R.id.businessDescription);
        this.minPrice = this.rootView.findViewById(R.id.descriptionMinPrice);
        this.pricingNote = this.rootView.findViewById(R.id.descriptionPricingNote);
        this.availability = this.rootView.findViewById(R.id.descriptionAvailability);
        this.website = this.rootView.findViewById(R.id.descriptionWebsite);
        this.rvServices = this.rootView.findViewById(R.id.descriptionServicesRv);
        this.rvReviews = this.rootView.findViewById(R.id.descriptionReviews);

        bind();
        configureAppBarScroll(appBarLayout);

        return this.rootView;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void bind() {

        this.reviewScore.setText(String.format("%.1f", listing.getReviewScore()));
        this.numberOfReviews.setText("(" + this.listing.getNumberOfReviews() + ")");
        this.ratingBar.setRating((float) this.listing.getReviewScore());
        this.description.setText(this.listing.getDescription());
        this.minPrice.setText("From $" + (int) this.listing.getMinPrice());
        this.pricingNote.setText(this.listing.getPricingNote());
        this.availability.setText(AvailabilityStatus.getAvailabilityText(this.listing.getAvailability()));
        this.website.setText(this.listing.getWebsite());
        TextView viewReviewsButton = this.rootView.findViewById(R.id.goToReviewButton);

        viewReviewsButton.setOnClickListener(this::viewReviews);
        if (this.listing.getNumberOfReviews() > 0) {
            configureReviews();
        } else {
            TextView noReviewTitle = this.rootView.findViewById(R.id.noReviewTitle);
            noReviewTitle.setVisibility(View.VISIBLE);
        }

        Log.d(TAG, "bind: " + this.listing.getServicesList().getServicesProvided());

        DescriptionCategoryAdapter adapter = new DescriptionCategoryAdapter(getActivity(),
                this.listing.getServicesList().getServicesProvided(), this);
        this.rvServices.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.HORIZONTAL, false));
        this.rvServices.setAdapter(adapter);
    }

    private void configureReviews() {
        PagedList.Config rvConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(3)
                .build();

        Query query = ReviewQuery.getPreview(FirebaseFirestore.getInstance(), this.listingId);
        FirestorePagingOptions<Review> options = new FirestorePagingOptions.Builder<Review>()
                .setLifecycleOwner(this)
                .setQuery(query, rvConfig, Review.class)
                .build();

        ReviewsAdapter adapter = new ReviewsAdapter(options);
        this.rvReviews.setLayoutManager(new LinearLayoutManager(requireActivity()));
        this.rvReviews.setAdapter(adapter);
    }

    private void configureAppBarScroll(AppBarLayout appBarLayout) {

        Toolbar backBar = this.rootView.findViewById(R.id.descriptionToolBar);
        backBar.setNavigationOnClickListener(x -> requireActivity().onBackPressed());

        RelativeLayout relativeLayout = this.rootView.findViewById(R.id.profileRelativeLayout);
        CollapsingToolbarLayout toolbar = this.rootView.findViewById(R.id.descriptionToolBarLayout);
        toolbar.setTitle(this.listing.getName());

        appBarLayout.addOnOffsetChangedListener((BarLayout, verticalOffset) -> {
            if (verticalOffset > -80) {
                relativeLayout.setVisibility(View.VISIBLE);
            } else {
                relativeLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void viewReviews(View v) {
        if (this.listing.getNumberOfReviews() == 0) {
            Toast.makeText(requireActivity(), "No reviews found", Toast.LENGTH_SHORT);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("id", this.listingId);
        bundle.putString(Services.SERVICE, getArguments().getString("category"));
        bundle.putDouble(Listings.FIELD_REVIEW_SCORE, this.listing.getReviewScore());
        Navigation.findNavController(v)
                .navigate(R.id.action_businessViewListing_to_viewReviewsFragment2, bundle);
    }

    @Override
    public void onCategoryClicked(String category, View v) {

    }
}