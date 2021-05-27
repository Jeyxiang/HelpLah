package com.example.helplah.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.CategoriesAdapter;
import com.example.helplah.models.Services;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class ServicesCategoriesActivity extends AppCompatActivity implements CategoriesAdapter.onCategorySelected {

    public static final String TAG = "ServicesCategoryActivities";

    RecyclerView recyclerView;
    MaterialSearchBar searchView;


    int[] categoriesImages = {R.drawable.ic_baseline_electrical_services_24, R.drawable.ic_baseline_plumbing_24,
            R.drawable.ic_baseline_cleaning_services_24, R.drawable.ic_baseline_home_repair_service_24,
            R.drawable.ic_baseline_airport_shuttle_24, R.drawable.ic_baseline_lock_24,
            R.drawable.ic_baseline_pest_control_24, R.drawable.plumbing_logos};

    String[] categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_categories);

        this.recyclerView = findViewById(R.id.categoryRecyclerView);
        searchView = findViewById(R.id.categorySearchView);
        categories = Services.ALLSERVICES.toArray(new String[0]);

        //handling the grid views
        CategoriesAdapter mAdapter = new CategoriesAdapter(this, categories, categoriesImages);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false);
        this.recyclerView.setAdapter(mAdapter);
        ViewCompat.setNestedScrollingEnabled(this.recyclerView, false);
        this.recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onCategoryClicked(String category) {
        Log.d(TAG, "onCategoryClicked: " + category);

        Intent intent = new Intent(this, BusinessListings.class);
        intent.putExtra(Services.SERVICE, category);

        startActivity(intent);
    }
}