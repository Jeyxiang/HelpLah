package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class BusinessListings extends AppCompatActivity implements
        ListingsDialogFragment.FilterListener,
        ListingsAdapter.onListingSelectedListener {

    private static final String TAG = "Business Listings Activity";

    private RecyclerView rvListings;
    private Toolbar listingsToolBar;
    private ListingsAdapter rvAdapter;
    private PagedList.Config rvConfig;

    private Query query;
    private ListingsDialogFragment filterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_listings);

        this.listingsToolBar = findViewById(R.id.searchTopAppBar);
        setToolBarListener(this.listingsToolBar);
        this.listingsToolBar.setNavigationOnClickListener(x -> finish());
        this.rvListings = findViewById(R.id.rvListings);

        // Create filter dialog
        setFinishOnTouchOutside(false);
        this.filterDialog = new ListingsDialogFragment();

        String category = getIntent().getExtras().getString(Services.SERVICE);
        ListingsQuery q = new ListingsQuery(FirebaseFirestore.getInstance());
        if (category != null) {
            q.setService(category);
            this.listingsToolBar.setTitle("Results for " + category + "s");
        } else {
            throw new IllegalArgumentException("Must pass a: " + Services.SERVICE);
        }

        q.setSortBy(Listings.FIELD_REVIEW_SCORE, false);
        this.query = q.createQuery();
        getQuery(this.query);
    }

    private void getQuery(Query q) {
        this.rvConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .setPageSize(5)
                .setPrefetchDistance(5)
                .build();

        FirestorePagingOptions<Listings> options = new FirestorePagingOptions.Builder<Listings>()
                .setLifecycleOwner(this)
                .setQuery(q, this.rvConfig, Listings.class)
                .build();

        this.rvAdapter = new ListingsAdapter(options, this);
        rvListings.setAdapter(rvAdapter);
        rvListings.setLayoutManager(new LinearLayoutManager(this));
        //checkIfEmptyQuery();
    }

    private void changeQuery(Query newQuery) {
        this.query = newQuery;

        FirestorePagingOptions<Listings> options = new FirestorePagingOptions.Builder<Listings>()
                .setLifecycleOwner(this)
                .setQuery(newQuery, this.rvConfig, Listings.class)
                .build();

        this.rvAdapter.updateOptions(options);
        //checkIfEmptyQuery();
    }

    private void checkIfEmptyQuery() {
        if (this.rvAdapter != null && this.rvAdapter.getItemCount() == 0) {
            Log.d(TAG, "checkIfEmptyQuery: No results");
            Snackbar.make(findViewById(R.id.listingsCoordinatorLayout), "No results found", Snackbar.LENGTH_SHORT).show();
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
                    Log.i(BusinessListings.TAG, "searching clicked");
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
        this.filterDialog.show(getSupportFragmentManager(), BusinessListings.TAG);
    }

    @Override
    public void onListingClicked(DocumentSnapshot listing) {
        // Go to listing description
        Log.d(TAG, "onListingClicked: " + listing.get(Listings.FIELD_NAME));
    }
}