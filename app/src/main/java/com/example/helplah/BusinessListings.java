package com.example.helplah;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.adapters.ListingsAdapter;
import com.example.helplah.models.Listings;
import com.example.helplah.models.ListingsQuery;
import com.example.helplah.models.Services;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class BusinessListings extends AppCompatActivity {

    private static final String TAG = "Business Listings Activity";

    private RecyclerView rvListings;
    private Toolbar listingsToolBar;

    private Query query;
    private ListingsDialogFragment filterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_listings);

        this.listingsToolBar = findViewById(R.id.searchTopAppBar);
        setToolBarListener(this.listingsToolBar);
        this.rvListings = findViewById(R.id.rvListings);

        // Create filter dialog
        setFinishOnTouchOutside(false);
        this.filterDialog = new ListingsDialogFragment();

        ListingsQuery q = new ListingsQuery(FirebaseFirestore.getInstance());
        q.setService(Services.ELECTRICIAN);
        q.setAvailability(1,2);
        q.setSortBy(Listings.FIELD_REVIEW_SCORE, false);
        this.query = q.createQuery();
        getQuery(this.query);
    }

    protected void getQuery(Query q) {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(15)
                .setPageSize(10)
                .setPrefetchDistance(5)
                .build();

        FirestorePagingOptions<Listings> options = new FirestorePagingOptions.Builder<Listings>()
                .setLifecycleOwner(this)
                .setQuery(q, config, Listings.class)
                .build();

        ListingsAdapter adapter = new ListingsAdapter(options);
        rvListings.setAdapter(adapter);
        rvListings.setLayoutManager(new LinearLayoutManager(this));
    }

    public void setToolBarListener(Toolbar toolbar) {
        toolbar.setOnMenuItemClickListener( menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.topBarSearch:
                    Log.i(BusinessListings.TAG, "searching clicked");
                    return true;
                case R.id.topBarFilter:
                    onFilterOptionClicked();
                    return true;
                default:
                    return false;
            }
        });
    }

    public void onFilterOptionClicked() {
        this.filterDialog.show(getSupportFragmentManager(), BusinessListings.TAG);
    }
}