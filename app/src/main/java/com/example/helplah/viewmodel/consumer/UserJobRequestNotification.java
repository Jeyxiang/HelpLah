package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.JobRequestsAdapter;
import com.example.helplah.models.JobRequests;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Fragment for displaying a single job request. It is opened when a user opens a job request
 * notification. The fragment will display the job request of the notification. It is similar to
 * the  job request fragment but only shows one job request.
 *
 * This fragment requires the following arguments in the bundle when navigating to it:
 * id - String: The id of the job request in the notification
 */
public class UserJobRequestNotification extends JobRequestsFragment {

    private FirestoreRecyclerOptions<JobRequests> options;
    private View rootView;
    private RecyclerView rvJobRequests;
    private JobRequestsAdapter rvAdapter;
    private String userId;
    private String requestId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.requestId = getArguments().getString("id");

        Query query = FirebaseFirestore.getInstance().collection(JobRequests.DATABASE_COLLECTION)
                .whereEqualTo(JobRequests.FIELD_ID, this.requestId);

        configureFirestore(query);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.rootView = inflater.inflate(R.layout.user_job_request_notification_fragment, container, false);
        this.rvJobRequests = this.rootView.findViewById(R.id.jobRequestsRv);
        this.rvJobRequests.setHasFixedSize(true);
        getQuery();

        ImageView backButton = this.rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        return this.rootView;
    }

    @Override
    public void configureFirestore(Query query) {
        this.options = new FirestoreRecyclerOptions.Builder<JobRequests>()
                .setQuery(query, JobRequests.class)
                .build();
    }

    @Override
    public void getQuery() {
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
}