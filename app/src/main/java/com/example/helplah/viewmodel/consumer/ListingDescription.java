package com.example.helplah.viewmodel.consumer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import androidx.core.view.ViewCompat;
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
import com.example.helplah.models.ChatDialogue;
import com.example.helplah.models.Listings;
import com.example.helplah.models.Review;
import com.example.helplah.models.ReviewQuery;
import com.example.helplah.models.Services;
import com.example.helplah.models.User;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListingDescription} factory method to
 * create an instance of this fragment.
 */
public class ListingDescription extends Fragment implements CategoriesAdapter.onCategorySelected {

    private static final String TAG = "Listing description";

    private Listings listing;
    private String listingId;

    private View rootView;
    private Bundle bundle;
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

        this.bundle = this.getArguments();
        this.listing = this.getArguments().getParcelable("listing");
        this.listingId = this.getArguments().getString("id");

        if (this.listing == null) {
            Toast.makeText(getActivity(), "An unexpected error occurred", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.rootView =  inflater.inflate(R.layout.fragment_listing_description, container, false);

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
        configureButton();

        return this.rootView;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void bind() {

        this.reviewScore.setText(String.format("%.1f", listing.getReviewScore()));
        this.numberOfReviews.setText("(" + this.listing.getNumberOfReviews() + ")");
        this.ratingBar.setRating((float) this.listing.getReviewScore());
        this.description.setText(this.listing.getDescription());
        this.minPrice.setText("From " + (int) this.listing.getMinPrice());
        this.pricingNote.setText(this.listing.getPricingNote());
        this.availability.setText(AvailabilityStatus.getAvailabilityText(this.listing.getAvailability()));
        this.website.setText(this.listing.getPricingNote());
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

    private void configureButton() {

        FloatingActionButton callButton = this.rootView.findViewById(R.id.descriptionCall);
        callButton.setOnClickListener(v -> {
            Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + listing.getPhoneNumber()));
            startActivity(call);
        });

        FloatingActionButton chatButton = this.rootView.findViewById(R.id.descriptionChat);
        chatButton.setOnClickListener(this::chatButtonClicked);

        ExtendedFloatingActionButton sendJobButton = this.rootView.findViewById(R.id.descriptionJobRequest);
        sendJobButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.sendJobRequestAction, this.bundle));
    }

    private void chatButtonClicked(View v) {
        CollectionReference users = FirebaseFirestore.getInstance().collection(User.DATABASE_COLLECTION);
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        users.document(userId).get().addOnSuccessListener(snapshot -> {
            User user = snapshot.toObject(User.class);
            ChatDialogue chatDialogue = new ChatDialogue(userId, Objects.requireNonNull(user).getUsername(),
                    listingId, listing.getName());
            ChatDialogue.goToChatChannel(chatDialogue, bundle -> {
                Navigation.findNavController(v).navigate(R.id.action_listingDescription_to_chatView, bundle);
            });
        });
    }

    private void viewReviews(View v) {
        if (this.listing.getNumberOfReviews() == 0) {
            View snackBar = this.rootView.findViewById(R.id.snackBarView);
            ViewCompat.setTranslationZ(snackBar, 10f);
            Snackbar.make(snackBar, "There are no reviews for this listing", Snackbar.LENGTH_SHORT).show();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("id", this.listingId);
        bundle.putString(Services.SERVICE, getArguments().getString("category"));
        bundle.putDouble(Listings.FIELD_REVIEW_SCORE, this.listing.getReviewScore());
        Navigation.findNavController(v).navigate(R.id.action_view_reviews, bundle);
    }

    @Override
    public void onCategoryClicked(String category, View v) {
        Log.d(TAG, "onCategoryClicked: " + category);
        Bundle bundle = new Bundle();
        bundle.putString(Services.SERVICE, category);
        Navigation.findNavController(v).navigate(R.id.action_listingDescription_search_new_category, bundle);
    }
}