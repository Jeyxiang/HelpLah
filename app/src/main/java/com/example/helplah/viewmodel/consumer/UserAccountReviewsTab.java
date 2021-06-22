package com.example.helplah.viewmodel.consumer;

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
import com.example.helplah.models.JobRequests;
import com.example.helplah.models.Review;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserAccountReviewsTab extends Fragment implements ReviewTabAdapter.OptionsListener {

    private FirestorePagingOptions<Review> options;
    private View rootView;
    private RecyclerView rvReviews;
    private PagedList.Config rvConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseFirestore.getInstance()
                .collectionGroup(Review.DATABASE_COLLECTION)
                .whereEqualTo(Review.FIELD_USER_ID, id)
                .orderBy(Review.FIELD_DATE_REVIEWED, Query.Direction.DESCENDING);
        configureFirestore(query);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.user_account_reviews_tab, container, false);
        this.rvReviews = this.rootView.findViewById(R.id.accountReviewsRv);
        createQuery();

        return this.rootView;
    }

    private void createQuery() {
        ReviewTabAdapter adapter = new ReviewTabAdapter(this.options, this, false);
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
        // Edit review
        FirebaseFirestore.getInstance().collection(JobRequests.DATABASE_COLLECTION)
                .document(review.getJobRequestId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        JobRequests requests = documentSnapshot.toObject(JobRequests.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(JobRequests.DATABASE_COLLECTION, requests);
                        bundle.putParcelable(Review.DATABASE_COLLECTION, review);
                        Navigation.findNavController(v)
                                .navigate(R.id.action_accountFragment_to_editReviewFragment, bundle);
                    }
                });
    }
}