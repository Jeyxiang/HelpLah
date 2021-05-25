package com.example.helplah;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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

import java.util.Arrays;

public class BusinessListings extends AppCompatActivity {

    private RecyclerView rvListings;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_listings);
        rvListings = findViewById(R.id.rvListings);
        ListingsQuery q = new ListingsQuery(FirebaseFirestore.getInstance());
        q.setService(Services.ELECTRICIAN);
        q.setAvailability(2);
        q.setSortBy(Listings.FIELD_NUMBER_OF_REVIEWS, false);
        this.query = q.createQuery().whereIn(Listings.FIELD_NAME, Arrays.asList("CURiEfhg company"));
//        this.query = FirebaseFirestore.getInstance().collection("Businesses")
//                        .whereArrayContains(Listings.FIELD_SERVICES_LIST, Services.ELECTRICIAN)
//                        .whereEqualTo(Listings.FIELD_AVAILABILITY, 1)
//                        .orderBy(Listings.FIELD_REVIEW_SCORE, Query.Direction.DESCENDING);
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
}