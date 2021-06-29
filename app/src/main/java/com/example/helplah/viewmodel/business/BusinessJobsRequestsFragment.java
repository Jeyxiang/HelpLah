package com.example.helplah.viewmodel.business;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.JobRequestsAdapter;
import com.example.helplah.models.ChatFunction;
import com.example.helplah.models.JobRequestQuery;
import com.example.helplah.models.JobRequests;
import com.example.helplah.models.NotificationHandler;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusinessJobsRequestsFragment} factory method to
 * create an instance of this fragment.
 */
public class BusinessJobsRequestsFragment extends Fragment implements 
        JobRequestsAdapter.RequestClickedListener,
        JobRequestFilterDialog.RequestFilterListener {

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
    private JobRequestFilterDialog filterDialog;
    private ActionMode mode;
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
        requestQuery.setSortBy(JobRequests.FIELD_DATE_OF_JOB, false);
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
        this.filterDialog = new JobRequestFilterDialog(this);

        setToolbar();
        getQuery();

        return this.rootView;
    }

    public void configureFirestore(Query query) {

        this.options = new FirestoreRecyclerOptions.Builder<JobRequests>()
                .setQuery(query, JobRequests.class)
                .build();
    }

    public void getQuery() {
        Log.d(TAG, "getQuery: Getting query");

        this.rvAdapter = new JobRequestsAdapter(this.options, this, true, this.rvJobRequests);
        this.rvAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        this.rvJobRequests.setAdapter(this.rvAdapter);

        this.rvJobRequests.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void setToolbar() {
        this.toolbar.setOnMenuItemClickListener(menuItem -> {
            if (this.mode != null) {
                this.mode.finish();
            }
            if (menuItem.getItemId() == R.id.topBarFilter) {
                sortOptionClicked();
                return true;
            } else if (menuItem.getItemId() == R.id.topBarSearch) {
                goToSearch();
                return true;
            } else if (menuItem.getItemId() == R.id.topBarGetNew) {
                changeSort();
                return true;
            } else if (menuItem.getItemId() == R.id.topBarDefault) {
                resetFilter();
                return true;
            } else if (menuItem.getItemId() == R.id.topBarRemoveCancelledRequest) {
                new MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.confirm_remove_cancelled_request)
                        .setMessage(R.string.irreversible_warning)
                        .setPositiveButton("Confirm", (dialog, which) -> removeAllCancelledRequest())
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
                return true;
            } else if (menuItem.getItemId() == R.id.topBarRemoveFinishedRequests) {
                new MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.confirm_remove_finished_request)
                        .setMessage(R.string.irreversible_warning)
                        .setPositiveButton("Confirm", (dialog, which) -> removeFinishedRequests())
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            } else if (menuItem.getItemId() == R.id.topBarRemoveOldRequests) {
                new MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.confirm_remove_old_request)
                        .setMessage(R.string.irreversible_warning)
                        .setPositiveButton("Confirm", (dialog, which) -> removeOldRequests())
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
                return true;
            }
            return false;
        });
    }

    // Deletes all cancelled requests (Does not actually delete the request, only prevents it from
    // being shown. Only the customer who made the original request can delete it from the database)
    public void removeAllCancelledRequest() {
        String businessId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference jobsCollection = db.collection(JobRequests.DATABASE_COLLECTION);
        WriteBatch batch = db.batch();

        jobsCollection.whereEqualTo(JobRequests.FIELD_BUSINESS_ID, businessId)
                .whereEqualTo(JobRequests.FIELD_STATUS, JobRequests.STATUS_CANCELLED)
                .limit(500)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        DocumentReference doc = snapshot.getReference();
                        batch.update(doc, JobRequests.FIELD_REMOVED, true);
                    }
                    batch.commit()
                            .addOnSuccessListener(unused -> {
                                Log.d(TAG, "removeAllCancelledRequest: Cancelled requests removed");
                                Toast.makeText(requireActivity(), "Cancelled requests removed",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, "onFailure: Unable to remove cancelled requests");
                                Toast.makeText(requireActivity(), "Unable to remove cancelled requests",
                                        Toast.LENGTH_SHORT).show();
                    });
                });
    }

    public void removeFinishedRequests() {
        String businessId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference jobsCollection = db.collection(JobRequests.DATABASE_COLLECTION);
        WriteBatch batch = db.batch();

        jobsCollection.whereEqualTo(JobRequests.FIELD_BUSINESS_ID, businessId)
                .whereEqualTo(JobRequests.FIELD_STATUS, JobRequests.STATUS_FINISHED)
                .limit(500)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        DocumentReference doc = snapshot.getReference();
                        batch.update(doc, JobRequests.FIELD_REMOVED, true);
                    }
                    batch.commit()
                            .addOnSuccessListener(unused -> {
                                Log.d(TAG, "removeFinishedRequests: Finished requests removed");
                                Toast.makeText(requireActivity(), "Finished requests removed",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, "onFailure: Unable to remove finished requests");
                                Toast.makeText(requireActivity(), "Unable to remove finished requests",
                                        Toast.LENGTH_SHORT).show();
                            });
                });
    }

    // Remove requests older than a week (Does not actually delete the request, only prevents it from
    // being shown. Only the customer who made the original request can delete it from the database)
    public void removeOldRequests() {
        String businessId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        long currentTime = System.currentTimeMillis();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference jobsCollection = db.collection(JobRequests.DATABASE_COLLECTION);
        WriteBatch batch = db.batch();

        jobsCollection.whereEqualTo(JobRequests.FIELD_BUSINESS_ID, businessId)
                .whereLessThanOrEqualTo(JobRequests.FIELD_DATE_OF_JOB,
                        new Date(currentTime - 7 * JobRequestFilterDialog.milliseconds))
                .limit(500)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        DocumentReference doc = snapshot.getReference();
                        batch.update(doc, JobRequests.FIELD_REMOVED, true);
                    }
                    batch.commit()
                            .addOnSuccessListener(unused -> {
                                Log.d(TAG, "removeOldRequests: Removed old requests");
                                Toast.makeText(requireActivity(), "Old requests removed",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, "onFailure: Unable to remove old requests");
                                Toast.makeText(requireActivity(), "Unable to remove cancelled requests",
                                        Toast.LENGTH_SHORT).show();
                            });
                });
    }

    private void changeSort() {
        JobRequestFilterDialog.RequestFilterViewModel viewModel = this.filterDialog.getViewModel();
        viewModel.setSpinnerPosition(2);
        viewModel.allUnchecked();
        JobRequestQuery query = new JobRequestQuery(FirebaseFirestore.getInstance(), this.userId, true);
        query.setSortBy(JobRequests.FIELD_DATE_CREATED, false);
        changeQuery(query.createQuery());
    }

    private void resetFilter() {
        changeQuery(this.filterDialog.reset(this.userId));
    }

    private void sortOptionClicked() {
        this.filterDialog.show(requireActivity().getSupportFragmentManager(), TAG);
    }

    private void changeQuery(Query query) {
        this.viewModel.setQuery(query);
        Log.d(TAG, "changeQuery: Query changed");
        configureFirestore(query);

        this.rvAdapter.updateOptions(this.options);
    }

    @Override
    public void onFilter(Query query) {
        changeQuery(query);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if( this.rvAdapter == null) {
            return;
        }
        this.rvAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if( this.rvAdapter == null) {
            return;
        }
        this.rvAdapter.stopListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.mode != null) {
            this.mode.finish();
        }
    }

    @Override
    public void configureSupportAction(ActionMode.Callback callback) {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        this.mode = activity.startSupportActionMode(callback);
    }

    @Override
    public void deleteSelection(ArrayList<String> arrayList) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference jobsCollection = db.collection(JobRequests.DATABASE_COLLECTION);
        WriteBatch batch = db.batch();
        for (String id : arrayList) {
            DocumentReference doc = jobsCollection.document(id);
            batch.update(doc, JobRequests.FIELD_REMOVED, true);
        }

        batch.commit()
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "deleteSelection: Selected requests removed");
                    Toast.makeText(requireActivity(), "Cancelled requests removed",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: Unable to remove selected requests");
                    Toast.makeText(requireActivity(), "Unable to remove selected requests",
                            Toast.LENGTH_SHORT).show();
                });
    }

    // Confirms the request and confirms a time
    @Override
    public void actionTwoClicked(View v, JobRequests requests, String requestId) {

        if (requests.getStatus() == JobRequests.STATUS_PENDING) {
            confirmJob(requests, requestId);
        } else if (requests.getStatus() == JobRequests.STATUS_CONFIRMED) {
            markJobAsFinished(requests, requestId);
        }
    }


    // Declines the request
    @Override
    public void actionOneClicked(JobRequests request, String documentId) {

        String[] declineReasons = {"Unable to make it on the requested date", "Lack of manpower",
                "Unable to carry out required job", "Others"};
        String[] selectedReason = {declineReasons[0]};

        // Ask for reason for cancellation
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Reason for cancellation")
                .setPositiveButton("Ok", (dialog, which) -> declineRequest(documentId,
                        selectedReason[0], request))
                .setSingleChoiceItems(declineReasons, 0, (dialog, which) ->
                        selectedReason[0] = declineReasons[which])
                .show();
    }

    @Override
    public void onChatClicked(View v, JobRequests request) {
        ChatFunction.createChat(request.getCustomerId(), request.getCustomerName(), getActivity());
    }

    private void declineRequest(String documentId, String message, JobRequests request) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(JobRequests.FIELD_DECLINE_MESSAGE, message);
        updates.put(JobRequests.FIELD_STATUS, JobRequests.STATUS_CANCELLED);
        CollectionReference db = FirebaseFirestore.getInstance().collection(JobRequests.DATABASE_COLLECTION);
        db.document(documentId).update(updates);
        request.setDeclineMessage(message);
        NotificationHandler.requestCancelled(request, false);
        Toast.makeText(requireActivity(), "Job request declined", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "declineRequest: " + message);
    }

    private void confirmJob(JobRequests request, String requestId) {
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
            request.setConfirmedTiming(time);
            collection.document(requestId).update(updates);
            NotificationHandler.requestConfirmed(request);
            Toast.makeText(getActivity(), "Job request confirmed", Toast.LENGTH_SHORT).show();
        });
    }

    private void markJobAsFinished(JobRequests request, String requestId) {
        CollectionReference collection = FirebaseFirestore.getInstance()
                .collection(JobRequests.DATABASE_COLLECTION);
        Map<String, Object> updates = new HashMap<>();
        updates.put(JobRequests.FIELD_STATUS, JobRequests.STATUS_FINISHED);
        collection.document(requestId).update(updates);
        NotificationHandler.requestFinished(request);
        Toast.makeText(getActivity(), "Job request marked as finished", Toast.LENGTH_SHORT).show();
    }

    private void goToSearch() {
        Navigation.findNavController(requireView()).navigate(
                R.id.action_businessJobsRequestsFragment_to_requestSearchFragment);
    }

}