package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.CategoriesAdapter;
import com.example.helplah.models.Services;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class ServicesCategoriesFragment extends Fragment implements CategoriesAdapter.onCategorySelected {

    public static final String TAG = "ServicesCategoryActivities";

    private RecyclerView recyclerView;
    private MaterialSearchBar searchView;
    private View rootview;


    int[] categoriesImages = {R.drawable.ic_baseline_electrical_services_24, R.drawable.ic_baseline_plumbing_24,
            R.drawable.ic_baseline_cleaning_services_24, R.drawable.ic_baseline_home_repair_service_24,
            R.drawable.ic_baseline_airport_shuttle_24, R.drawable.ic_baseline_lock_24,
            R.drawable.ic_baseline_pest_control_24, R.drawable.ic_baseline_format_paint_24,
            R.drawable.ic_baseline_local_car_wash_24, R.drawable.ic_baseline_local_laundry_service_24};

    String[] categories;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootview = inflater.inflate(R.layout.services_categories_fragment, container, false);

        MainActivity activity = (MainActivity) getActivity();
        this.recyclerView = this.rootview.findViewById(R.id.categoryRecyclerView);
        this.searchView = this.rootview.findViewById(R.id.categorySearchView);
        this.categories = Services.ALLSERVICES.toArray(new String[0]);

        //handling the grid views
        CategoriesAdapter mAdapter = new CategoriesAdapter(getActivity(), categories, categoriesImages, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2,
                GridLayoutManager.VERTICAL, false);

        this.recyclerView.setAdapter(mAdapter);
        ViewCompat.setNestedScrollingEnabled(this.recyclerView, false);
        this.recyclerView.setLayoutManager(gridLayoutManager);
        return this.rootview;
    }

    @Override
    public void onCategoryClicked(String category, View v) {
        Log.d(TAG, "onCategoryClicked: " + category);

        Bundle bundle = new Bundle();
        bundle.putString(Services.SERVICE, category);
        Navigation.findNavController(v).navigate(R.id.goToListingsAction, bundle);
    }
}