package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.JobRequestsAdapter;
import com.example.helplah.models.JobRequestQuery;
import com.example.helplah.models.JobRequests;
import com.example.helplah.models.Listings;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobRequestsFragment} factory method to
 * create an instance of this fragment.
 */
public class JobRequestsFragment extends Fragment implements JobRequestsAdapter.RequestClickedListener {

    public static final String TAG = "Job Request Fragment";

    public static class JobRequestsViewModel extends ViewModel {

        private Query query;

        public void setQuery(Query query) {
            this.query = query;
        }

        public Query getQuery() {
            return this.query;
        }
    }

    private JobRequestsViewModel viewModel;
    private FirestoreRecyclerOptions<JobRequests> options;
    private View rootView;
    private RecyclerView rvJobRequests;
    private JobRequestsAdapter rvAdapter;
    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        JobRequestQuery requestQuery = new JobRequestQuery(FirebaseFirestore.getInstance(), this.userId);
        this.viewModel = new ViewModelProvider(this).get(JobRequestsViewModel.class);

        requestQuery.setSortBy(JobRequests.FIELD_DATE_OF_JOB);
        this.viewModel.setQuery(requestQuery.createQuery());

        configureFirestore(this.viewModel.getQuery());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.fragment_job_requests, container, false);

        this.rvJobRequests = this.rootView.findViewById(R.id.jobRequestsRv);
        this.rvJobRequests.setHasFixedSize(true);
        getQuery();

        return this.rootView;
    }

    private void configureFirestore(Query query) {

        this.options = new FirestoreRecyclerOptions.Builder<JobRequests>()
                .setQuery(query, JobRequests.class)
                .build();
    }

    private void getQuery() {
        Log.d(TAG, "getQuery: Getting query");

        this.rvAdapter = new JobRequestsAdapter(this.options, this, false, this.rvJobRequests);
        this.rvAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        this.rvJobRequests.setAdapter(this.rvAdapter);

        this.rvJobRequests.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        this.rvAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.rvAdapter.stopListening();
    }

    @Override
    public void onEditClicked(View v, JobRequests requests, String requestId) {

        CollectionReference listingsReference = FirebaseFirestore.getInstance().collection(Listings.DATABASE_COLLECTION);

        listingsReference.whereEqualTo(FieldPath.documentId(), requests.getBusinessId()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("listing", snapshot.toObject(Listings.class));
                        bundle.putParcelable("request", requests);
                        bundle.putString("id", requests.getBusinessId());
                        bundle.putString("requestId", requestId);
                        bundle.putString("category", requests.getService());
                        Navigation.findNavController(v).navigate(R.id.editJobRequestAction, bundle);
                    }
                });
    }
}