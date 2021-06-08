package com.example.helplah.viewmodel.business;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.JobRequestsAdapter;
import com.example.helplah.models.JobRequestQuery;
import com.example.helplah.models.JobRequests;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusinessJobsRequestsFragment} factory method to
 * create an instance of this fragment.
 */
public class BusinessJobsRequestsFragment extends Fragment implements JobRequestsAdapter.RequestClickedListener{

    private static final String TAG = "Business job request fragment";

    public static class BusinessJobRequestViewModel extends ViewModel {

        private Query query;

        public void setQuery(Query query) {
            this.query = query;
        }

        public Query getQuery() {
            return this.query;
        }
    }

    private BusinessJobRequestViewModel viewModel;
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
        this.viewModel = new ViewModelProvider(this).get(BusinessJobRequestViewModel.class);

        if (this.viewModel.getQuery() != null) {
            Log.d(TAG, "onCreate: Old query found " + this.viewModel.getQuery());
            configureFirestore(this.viewModel.getQuery());
            return;
        }

        JobRequestQuery requestQuery = new JobRequestQuery(FirebaseFirestore.getInstance(),
                this.userId, true);
        requestQuery.setSortBy(JobRequests.FIELD_DATE_OF_JOB);
        this.viewModel.setQuery(requestQuery.createQuery());

        configureFirestore(this.viewModel.getQuery());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.fragment_business_jobs_requests, container, false);

        this.toolbar = this.rootView.findViewById(R.id.businessRequestToolbar);
        this.rvJobRequests = this.rootView.findViewById(R.id.businessJobRequestsRv);
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

        this.rvAdapter = new JobRequestsAdapter(this.options, this, true, this.rvJobRequests);
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
    }

    private void changeQuery(boolean sortByDateOfJob) {

        JobRequestQuery requestQuery = new JobRequestQuery(FirebaseFirestore.getInstance(), this.userId, true);
        if (sortByDateOfJob) {
            requestQuery.setSortBy(JobRequests.FIELD_DATE_OF_JOB);
        } else {
            requestQuery.setSortBy(JobRequests.FIELD_DATE_CREATED);
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

    @Override
    public void onChatClicked() {
        // TODO
    }


    // Confirms the request and confirms a time
    @Override
    public void actionTwoClicked(View v, JobRequests requests, String requestId) {

        if (requests.getStatus() == JobRequests.STATUS_CONFIRMED) {
            Toast.makeText(getActivity(), "Job request has already been confirmed", Toast.LENGTH_SHORT).show();
            return;
        }
        MaterialTimePicker timePicker =
                new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select job time")
                .build();

        timePicker.show(getParentFragmentManager(), TAG);

        timePicker.addOnPositiveButtonClickListener(view -> {
            Log.d(TAG, "actionTwoClicked: clicked");
            @SuppressLint("DefaultLocale")
            String time = String.format("%02d", timePicker.getHour()) + ":" +
                    String.format("%02d", timePicker.getMinute());
            CollectionReference collection = FirebaseFirestore.getInstance()
                    .collection(JobRequests.DATABASE_COLLECTION);
            Map<String, Object> updates = new HashMap<>();
            updates.put(JobRequests.FIELD_CONFIRMED_TIMING, time);
            updates.put(JobRequests.FIELD_STATUS, JobRequests.STATUS_CONFIRMED);
            collection.document(requestId).update(updates);
            Toast.makeText(getActivity(), "Job request confirmed", Toast.LENGTH_SHORT).show();
        });

    }


    // Declines the request
    @Override
    public void actionOneClicked(JobRequests request, String documentId) {

        String[] declineReasons = {"Unable to make it on the requested date", "Lack of manpower",
                "Unable to carry out required job", "Others"};
        String[] selectedReason = {declineReasons[0]};

        if (request.getStatus() == JobRequests.STATUS_CANCELLED) {
            Toast.makeText(getActivity(), "Job request has already been cancelled", Toast.LENGTH_SHORT).show();
            return;
        }
        // Ask for reason for cancellation
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Reason for cancellation")
                .setPositiveButton("Ok", (dialog, which) -> declineRequest(documentId,
                        selectedReason[0]))
                .setSingleChoiceItems(declineReasons, 0, (dialog, which) ->
                        selectedReason[0] = declineReasons[which])
                .show();
    }

    private void declineRequest(String documentId, String message) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(JobRequests.FIELD_DECLINE_MESSAGE, message);
        updates.put(JobRequests.FIELD_STATUS, JobRequests.STATUS_CANCELLED);
        CollectionReference db = FirebaseFirestore.getInstance().collection(JobRequests.DATABASE_COLLECTION);
        db.document(documentId).update(updates);
        Toast.makeText(getActivity(), "Job request declined", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "declineRequest: " + message);
    }

}