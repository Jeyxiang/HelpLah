package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.ListingsAdapter;
import com.example.helplah.models.Listings;
import com.example.helplah.models.ListingsQuery;
import com.example.helplah.models.Services;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class BusinessListingsFragment extends Fragment implements
        ListingsDialogFragment.FilterListener,
        ListingsAdapter.onListingSelectedListener {

    private static final String TAG = "Business Listings Activity";

    public static class ListingViewModel extends ViewModel {

        private Query query;

        public void setQuery(Query query) {
            this.query = query;
        }

        public Query getQuery() {
            return this.query;
        }
    }

    private ListingViewModel mViewModel;
    private View rootview;
    private RecyclerView rvListings;
    private Toolbar listingsToolBar;
    private ListingsAdapter rvAdapter;
    private PagedList.Config rvConfig;
    private String title;
    private String category;

    FirestorePagingOptions<Listings> options;
    private ListingsDialogFragment filterDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.category = this.getArguments().getString(Services.SERVICE);

        ListingsQuery q = new ListingsQuery(FirebaseFirestore.getInstance());

        if (this.category != null) {
            q.setService(category);
            this.title = "Results for " + this.category + "s";
        } else {
            throw new IllegalArgumentException("Must pass a: " + Services.SERVICE);
        }

        this.mViewModel = new ViewModelProvider(this).get(ListingViewModel.class);

        if (this.mViewModel.getQuery() != null) {
            Log.d(TAG, "onCreate: saved query found " + this.mViewModel.getQuery());
            configureFirestore(this.mViewModel.getQuery());
            return;
        }

        q.setSortBy(Listings.FIELD_REVIEW_SCORE, false);
        this.mViewModel.setQuery(q.createQuery());
        configureFirestore(this.mViewModel.getQuery());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.rootview = inflater.inflate(R.layout.business_listings_fragment, container, false);

        this.listingsToolBar = this.rootview.findViewById(R.id.searchTopAppBar);
        this.listingsToolBar.setTitle(this.title);

        setToolBarListener(this.listingsToolBar);
        this.listingsToolBar.setNavigationOnClickListener(x -> requireActivity().onBackPressed());
        this.rvListings = this.rootview.findViewById(R.id.rvListings);

        // Create filter dialog
        this.filterDialog = new ListingsDialogFragment(this, this.category);

        getQuery();

        return this.rootview;
    }

    private void configureFirestore(Query q) {
        this.rvConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(20)
                .setPageSize(10)
                .setPrefetchDistance(10)
                .build();

        this.options = new FirestorePagingOptions.Builder<Listings>()
                .setLifecycleOwner(this)
                .setQuery(q, this.rvConfig, Listings.class)
                .build();
    }


    private void getQuery() {
        this.rvAdapter = new ListingsAdapter(this.options, this);
        this.rvAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        rvListings.setAdapter(rvAdapter);

        rvListings.setLayoutManager(new LinearLayoutManager(getActivity()));
        //checkIfEmptyQuery();
    }

    private void changeQuery(Query newQuery) {
        this.mViewModel.setQuery(newQuery);
        Log.d(TAG, "changeQuery: " + newQuery);

        this.options = new FirestorePagingOptions.Builder<Listings>()
                .setLifecycleOwner(this)
                .setQuery(newQuery, this.rvConfig, Listings.class)
                .build();

        this.rvAdapter.updateOptions(options);
        //checkIfEmptyQuery();
    }

    private void checkIfEmptyQuery() {
        if (this.rvAdapter != null && this.rvAdapter.getItemCount() == 0) {
            Log.d(TAG, "checkIfEmptyQuery: No results");
            Snackbar.make(this.rootview.findViewById(R.id.listingsCoordinatorLayout), "No results found", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFilter(Query query) {
        changeQuery(query);
    }

    public void setToolBarListener(Toolbar toolbar) {
        toolbar.setOnMenuItemClickListener( menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.topBarSearch:
                    Log.i(BusinessListingsFragment.TAG, "searching clicked");
                    onSearchOptionClicked();
                    return true;
                case R.id.topBarFilter:
                    onFilterOptionClicked();
                    return true;
                default:
                    return false;
            }
        });
    }

    public void onSearchOptionClicked() {
        // TODO
    }

    public void onFilterOptionClicked() {
        this.filterDialog.show(getActivity().getSupportFragmentManager(), BusinessListingsFragment.TAG);
    }

    @Override
    public void onListingClicked(DocumentSnapshot listing, View v) {
        // Go to listing description
        Log.d(TAG, "onListingClicked: " + listing.getId());

        Bundle bundle = new Bundle();
        Listings selectedListing = listing.toObject(Listings.class);
        bundle.putParcelable("listing", selectedListing);
        bundle.putString("id", listing.getId());
        bundle.putString("category", this.category);
        Navigation.findNavController(v).navigate(R.id.goToListingsDescription, bundle);
    }
}