package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobRequestsFragment} factory method to
 * create an instance of this fragment.
 */
public class JobRequestsFragment extends Fragment implements JobRequestsAdapter.RequestClickedListener {

    private static final String TAG = "Job Request Fragment";

    public static class JobRequestsViewModel extends ViewModel {

        private Query query;
        private int toolbarSelectedPosition = 0;

        public int getToolbarSelectedPosition() {
            return toolbarSelectedPosition;
        }

        public void setToolbarSelectedPosition(int toolbarSelectedPosition) {
            this.toolbarSelectedPosition = toolbarSelectedPosition;
        }

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
    private MaterialToolbar toolbar;
    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.viewModel = new ViewModelProvider(this).get(JobRequestsViewModel.class);

        if (this.viewModel.getQuery() != null) {
            Log.d(TAG, "onCreate: Old query found " + this.viewModel.getQuery());
            configureFirestore(this.viewModel.getQuery());
            return;
        }

        JobRequestQuery requestQuery = new JobRequestQuery(FirebaseFirestore.getInstance(), this.userId, false);
        requestQuery.setSortBy(JobRequests.FIELD_DATE_OF_JOB, false);
        this.viewModel.setQuery(requestQuery.createQuery());

        configureFirestore(this.viewModel.getQuery());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.fragment_job_requests, container, false);

        this.toolbar = this.rootView.findViewById(R.id.requestToolbar);
        this.rvJobRequests = this.rootView.findViewById(R.id.jobRequestsRv);
        this.rvJobRequests.setHasFixedSize(true);

        setToolbar();
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

    private void setToolbar() {
        this.toolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.topBarSort) {
                sortOptionClicked();
                return true;
            }
            return false;
        });
    }

    private void sortOptionClicked() {

        String[] sortOptions = {"Date of Job", "Date created"};

        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Sort by?")
                .setSingleChoiceItems(sortOptions, this.viewModel.getToolbarSelectedPosition(),
                        ((dialog, which) -> {
                            if (which == 0) { // Sort by date of job
                                changeQuery(true);
                                this.viewModel.setToolbarSelectedPosition(0);
                                dialog.dismiss();
                            } else { // sort by date created
                                changeQuery(false);
                                this.viewModel.setToolbarSelectedPosition(1);
                                dialog.dismiss();
                            }
                        })).show();
    }

    private void changeQuery(boolean sortByDateOfJob) {

        JobRequestQuery requestQuery = new JobRequestQuery(FirebaseFirestore.getInstance(), this.userId, false);
        if (sortByDateOfJob) {
            requestQuery.setSortBy(JobRequests.FIELD_DATE_OF_JOB, false);
        } else {
            requestQuery.setSortBy(JobRequests.FIELD_DATE_CREATED, false);
        }

        this.viewModel.setQuery(requestQuery.createQuery());
        Log.d(TAG, "changeQuery: Query changed");
        configureFirestore(requestQuery.createQuery());

        this.rvAdapter.updateOptions(this.options);
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

    // Edits the request
    @Override
    public void actionTwoClicked(View v, JobRequests requests, String requestId) {

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


    // Cancels the request
    @Override
    public void actionOneClicked(JobRequests request, String documentId) {
        request.setStatus(JobRequests.STATUS_CANCELLED);
        CollectionReference db = FirebaseFirestore.getInstance().collection(JobRequests.DATABASE_COLLECTION);
        Map<String, Object> status = new HashMap<>();
        status.put(JobRequests.FIELD_STATUS, JobRequests.STATUS_CANCELLED);
        db.document(documentId).update(status);
        Toast.makeText(getActivity(), "Request has been cancelled", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "cancelClicked: " + documentId + " status updated");
    }

    @Override
    public void onChatClicked() {

    }
}