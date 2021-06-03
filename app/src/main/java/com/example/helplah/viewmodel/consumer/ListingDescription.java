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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.DescriptionCategoryAdapter;
import com.example.helplah.models.AvailabilityStatus;
import com.example.helplah.models.Listings;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListingDescription} factory method to
 * create an instance of this fragment.
 */
public class ListingDescription extends Fragment {

    private static final String TAG = "Listing description";

    private Listings listing;

    private View rootView;
    private Bundle bundle;
    private RecyclerView rvServices;
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

        bind();
        configureAppBarScroll(appBarLayout);
        configureButton();

        return this.rootView;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void bind() {

        this.reviewScore.setText(String.format("%.1f", listing.getReviewScore()));
        this.numberOfReviews.setText(this.listing.getNumberOfReviews() + " reviews");
        this.ratingBar.setRating((float) this.listing.getNumberOfReviews());
        this.description.setText(this.listing.getDescription());
        this.minPrice.setText("From " + (int) this.listing.getMinPrice());
        this.pricingNote.setText(this.listing.getPricingNote());
        this.availability.setText(AvailabilityStatus.getAvailabilityText(this.listing.getAvailability()));
        this.website.setText(this.listing.getPricingNote());

        Log.d(TAG, "bind: " + this.listing.getServicesList().getServicesProvided());

        DescriptionCategoryAdapter adapter = new DescriptionCategoryAdapter(getActivity(),
                this.listing.getServicesList().getServicesProvided());
        this.rvServices.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.HORIZONTAL, false));
        this.rvServices.setAdapter(adapter);
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

        ExtendedFloatingActionButton sendJobButton = this.rootView.findViewById(R.id.descriptionJobRequest);
        sendJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.sendJobRequestAction, bundle);
            }
        });
    }
}