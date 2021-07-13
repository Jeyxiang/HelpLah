package com.example.helplah.viewmodel.business;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.ReviewTabAdapter;
import com.example.helplah.models.Listings;
import com.example.helplah.models.Review;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Fragment that shows all the reviews received by the business. This fragment is meant to be
 * used in a viewpager in the BusinessAccountFragment tab.
 */
public class BusinessAccountReviewsTab extends Fragment implements ReviewTabAdapter.OptionsListener {

    private FirestorePagingOptions<Review> options;
    private View rootView;
    private RecyclerView rvReviews;
    private PagedList.Config rvConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseFirestore.getInstance()
                .collection(Listings.DATABASE_COLLECTION)
                .document(id)
                .collection(Review.DATABASE_COLLECTION)
                .orderBy(Review.FIELD_DATE_REVIEWED, Query.Direction.DESCENDING);
        configureFirestore(query);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.business_account_reviews_tab, container, false);
        this.rvReviews = this.rootView.findViewById(R.id.accountReviewsRv);
        createQuery();

        return this.rootView;
    }

    private void createQuery() {
        ReviewTabAdapter adapter = new ReviewTabAdapter(this.options, this, true);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        this.rvReviews.setLayoutManager(new LinearLayoutManager(requireActivity()));
        this.rvReviews.setAdapter(adapter);
    }

    private void configureFirestore(Query query) {
        this.rvConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(30)
                .setPageSize(10)
                .setPrefetchDistance(10)
                .build();

        this.options = new FirestorePagingOptions.Builder<Review>()
                .setLifecycleOwner(this)
                .setQuery(query, this.rvConfig, Review.class)
                .build();
    }

    @Override
    public void optionClicked(View v, Review review) {
        // Reply review
        Bundle bundle = new Bundle();
        bundle.putParcelable("review", review);
        Navigation.findNavController(v)
                .navigate(R.id.action_businessAccountFragment_to_replyReviewFragment, bundle);
    }
}