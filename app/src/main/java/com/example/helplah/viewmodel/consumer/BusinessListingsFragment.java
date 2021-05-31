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

import org.jetbrains.annotations.NotNull;

public class BusinessListingsFragment extends Fragment implements
        ListingsDialogFragment.FilterListener,
        ListingsAdapter.onListingSelectedListener {

    private static final String TAG = "Business Listings Activity";

    private View rootview;
    private RecyclerView rvListings;
    private Toolbar listingsToolBar;
    private ListingsAdapter rvAdapter;
    private PagedList.Config rvConfig;
    private String title;
    private RecyclerView.LayoutManager layout;

    private Query query;
    FirestorePagingOptions<Listings> options;
    private ListingsDialogFragment filterDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate: Saved instance state is not null");
        } else {
            Log.d(TAG, "onCreate: Saved instance state is null");
        }

        String category = this.getArguments().getString(Services.SERVICE);

        ListingsQuery q = new ListingsQuery(FirebaseFirestore.getInstance());

        if (category != null) {
            q.setService(category);
            this.title = "Results for " + category + "s";
        } else {
            throw new IllegalArgumentException("Must pass a: " + Services.SERVICE);
        }

        q.setSortBy(Listings.FIELD_REVIEW_SCORE, false);
        this.query = q.createQuery();
        configureFirestore(this.query);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: " + "creating view");

        this.rootview = inflater.inflate(R.layout.business_listings_fragment, container, false);

        this.listingsToolBar = this.rootview.findViewById(R.id.searchTopAppBar);
        this.listingsToolBar.setTitle(this.title);

        setToolBarListener(this.listingsToolBar);
        this.listingsToolBar.setNavigationOnClickListener(x -> requireActivity().onBackPressed());
        this.rvListings = this.rootview.findViewById(R.id.rvListings);

        // Create filter dialog
        //setFinishOnTouchOutside(false);
        this.filterDialog = new ListingsDialogFragment(this);

        getQuery();

        return this.rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
    }

    @Override
    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored: called");
    }

    private void configureFirestore(Query q) {
        this.rvConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(30)
                .setPageSize(10)
                .setPrefetchDistance(10)
                .build();

        this.options = new FirestorePagingOptions.Builder<Listings>()
                .setLifecycleOwner(this)
                .setQuery(q, this.rvConfig, Listings.class)
                .build();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("state_key", this.layout.onSaveInstanceState());
    }


    private void getQuery() {
        this.rvAdapter = new ListingsAdapter(this.options, this);
        this.rvAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        rvListings.setAdapter(rvAdapter);
        layout = new LinearLayoutManager(getActivity());
        rvListings.setLayoutManager(layout);
        //checkIfEmptyQuery();
    }

    private void changeQuery(Query newQuery) {
        this.query = newQuery;
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
        Log.d(TAG, "onListingClicked: " + listing.get(Listings.FIELD_NAME));
        Navigation.findNavController(v).navigate(R.id.goToListingsDescription);
    }
}