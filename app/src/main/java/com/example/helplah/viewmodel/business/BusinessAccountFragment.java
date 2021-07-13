package com.example.helplah.viewmodel.business;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.helplah.R;
import com.example.helplah.adapters.AccountPagerAdapter;
import com.example.helplah.models.Listings;
import com.example.helplah.models.ProfilePictureHandler;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Fragment for displaying the account tab of a business user. Contains a viewpager which
 * allows a user to swipe between the notifications and reviews tab.
 */
public class BusinessAccountFragment extends Fragment {

    private static final String TAG = "Business account fragment";

    public static class BusinessAccountViewModel extends ViewModel {

        Listings listings;

        public Listings getListings() {
            return listings;
        }

        public void setListings(Listings listings) {
            this.listings = listings;
        }
    }

    private View rootView;
    private BusinessAccountViewModel viewModel;
    private TextView accountName;
    private TextView accountScore;
    private TextView accountNumberOfReviews;
    private CircleImageView accountPicture;
    private MaterialRatingBar ratingBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.fragment_business_account, container, false);
        this.viewModel = new ViewModelProvider(this).get(BusinessAccountViewModel.class);

        AppBarLayout appBarLayout = this.rootView.findViewById(R.id.accountAppBar);
        configureAppBarScroll(appBarLayout);
        this.accountName = this.rootView.findViewById(R.id.accountName);
        this.accountScore = this.rootView.findViewById(R.id.accountScore);
        this.accountNumberOfReviews = this.rootView.findViewById(R.id.accountNumberOfReviews);
        this.accountPicture = this.rootView.findViewById(R.id.accountProfilePicture);
        this.ratingBar = this.rootView.findViewById(R.id.accountRatingBar);

        getListingFromDatabase();
        configureViewPager();


        return rootView;
    }

    private void getListingFromDatabase() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ProfilePictureHandler.setProfilePicture(this.accountPicture, id, requireActivity());

        if (this.viewModel.getListings() != null) {
            bindData();
            configureButtons();
            return;
        }

        DocumentReference doc = FirebaseFirestore.getInstance()
                .collection(Listings.DATABASE_COLLECTION).document(id);

        doc.get().addOnSuccessListener(documentSnapshot -> {
            Listings listing = documentSnapshot.toObject(Listings.class);
            viewModel.setListings(listing);
            bindData();
            configureButtons();
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void bindData() {
        Listings listing = this.viewModel.getListings();
        this.accountName.setText(listing.getName());
        this.accountScore.setText(String.format("%.1f", listing.getReviewScore()));
        this.accountNumberOfReviews.setText("(" + listing.getNumberOfReviews() + " reviews)");
        this.ratingBar.setRating((float) listing.getReviewScore());
    }

    private void configureButtons() {
        Button editListingButton = this.rootView.findViewById(R.id.accountEditListingButton);
        Button viewListingButton = this.rootView.findViewById(R.id.accountViewListingButton);
        ImageView settingsButton = this.rootView.findViewById(R.id.accountSettingsButton);

        editListingButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_edit_listing));

        viewListingButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("listing", viewModel.getListings());
            bundle.putString("id", viewModel.getListings().getListingId());
            Navigation.findNavController(v)
                    .navigate(R.id.action_businessAccountFragment_to_businessViewListing, bundle);
        });

        settingsButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_go_to_business_Settings));
    }

    private void configureViewPager() {
        ViewPager2 viewPager = this.rootView.findViewById(R.id.businessAccountViewPager);
        TabLayout tabLayout = this.rootView.findViewById(R.id.businessAccountTabLayout);
        AccountPagerAdapter accountPagerAdapter = new AccountPagerAdapter(this, true);
        viewPager.setAdapter(accountPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(position == 0 ? "My reviews" : "Notifications")
        ).attach();
    }

    private void configureAppBarScroll(AppBarLayout appBarLayout) {

        ConstraintLayout constraintLayout = this.rootView.findViewById(R.id.accountConstraintLayout);

        appBarLayout.addOnOffsetChangedListener((BarLayout, verticalOffset) -> {
            if (verticalOffset > -80) {
                constraintLayout.setVisibility(View.VISIBLE);
            } else {
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });
    }
}