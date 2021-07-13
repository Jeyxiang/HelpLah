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
 * A fragment that shows a list of the business job requests. It uses a recycler view to display the
 * job requests and also hosts a dialog fragment that allows a business user to filter and sort
 * the job requests displayed.
 */
public class BusinessJobsRequestsFragment extends Fragment implements 
        JobRequestsAdapter.RequestClickedListener,
        JobRequestFilterDialog.RequestFilterListener {

    private static final String TAG = "Business job request fragment";

    /**
     * A viewModel to save the query so that it survives configuration changes. This means
     * that when a user switches tabs in the bottom navigation bar, the query is saved so that
     * the results does not change when the user returns to this fragment.
     */
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

        // Creates the default query nn which the job requests are sorted by the date of job
        // in descending order
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

    /**
     * Sets up the firestore options for the recycler view.
     * @param query The firestore query to be displayed in the recycler view.
     */
    public void configureFirestore(Query query) {

        this.options = new FirestoreRecyclerOptions.Builder<JobRequests>()
                .setQuery(query, JobRequests.class)
                .build();
    }

    /**
     * Configures the recycler view by creating an adapter to display the query results.
     */
    public void getQuery() {
        Log.d(TAG, "getQuery: Getting query");

        this.rvAdapter = new JobRequestsAdapter(this.options, this, true, this.rvJobRequests);
        this.rvAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        this.rvJobRequests.setAdapter(this.rvAdapter);

        this.rvJobRequests.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * Configures the recycler view by creating an adapter to display the query results.
     */
    public void setToolbar() {
        this.toolbar.setOnMenuItemClickListener(menuItem -> {
            if (this.mode != null) {
                this.mode.finish();
            }
            if (menuItem.getItemId() == R.id.topBarFilter) {
                sortOptionClicked();
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

    /**
     * Deletes all the job request that the business or consumer has previously cancelled. This method
     * does not delete the job request from the backend database but rather marks it as removed,
     * preventing the business user from seeing it.
     */
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

    /**
     * Deletes all the finished job request. This method does not delete the job
     * request from the backend database but rather marks it as removed, preventing the user from
     * seeing it.
     */
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

    /**
     * Deletes all the job requests that are older than a week. This methods will delete all
     * old request regardless of the status. This method does not delete the job request from the
     * backend database but rather marks it as removed, preventing the user from seeing it.
     */
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

    /**
     * Allows the business user to quickly sort the job requests by date created rether than
     * date of job.
     */
    private void changeSort() {
        JobRequestFilterDialog.RequestFilterViewModel viewModel = this.filterDialog.getViewModel();
        viewModel.setSpinnerPosition(2);
        viewModel.allUnchecked();
        JobRequestQuery query = new JobRequestQuery(FirebaseFirestore.getInstance(), this.userId, true);
        query.setSortBy(JobRequests.FIELD_DATE_CREATED, false);
        changeQuery(query.createQuery());
    }

    /**
     * Resets the filter dialog.
     */
    private void resetFilter() {
        changeQuery(this.filterDialog.reset(this.userId));
    }

    /**
     * Opens up the filter dialog fragment when the sort option button is clicked.
     */
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

    /**
     * Deletes and cancels all the selected item that are selected by the user in multi-select mode.
     * Job requests that have status as confirmed cannot be deleted this way. They have to be deleted
     * manually. This method does not delete the job request from the backend database but rather
     * marks it as removed, preventing the user from seeing it.
     * @param arrayList The list of all the job requests to delete.
     */
    @Override
    public void deleteSelection(ArrayList<JobRequests> arrayList) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference jobsCollection = db.collection(JobRequests.DATABASE_COLLECTION);
        WriteBatch batch = db.batch();
        for (JobRequests request : arrayList) {
            if (request.getStatus() == JobRequests.STATUS_CONFIRMED) {
                continue;
            }
            DocumentReference doc = jobsCollection.document(request.getId());
            batch.update(doc, JobRequests.FIELD_REMOVED, true);
            if (request.getStatus() == JobRequests.STATUS_PENDING) {
                request.setDeclineMessage("Others");
                NotificationHandler.requestCancelled(request, false);
                batch.update(doc, JobRequests.FIELD_STATUS, JobRequests.STATUS_CANCELLED);
                batch.update(doc, JobRequests.FIELD_DECLINE_MESSAGE, "Others");
            }
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

    @Override
    public void actionTwoClicked(View v, JobRequests requests, String requestId) {

        if (requests.getStatus() == JobRequests.STATUS_PENDING) {
            confirmJob(requests, requestId);
        } else if (requests.getStatus() == JobRequests.STATUS_CONFIRMED) {
            markJobAsFinished(requests, requestId);
        }
    }

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

    /**
     * Allows a business user to decline a request. The request status is changed to cancelled and
     * a decline reason the user selected is added to the request. The changes are then updated to
     * the database and a notification will be send to the user who made the job request.
     * @param documentId The id of the job request to be declined.
     * @param message The decline message that the business user has selected.
     * @param request The request to be declined.
     */
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

    /**
     * Confirms the job request. Shows a time picker where the user can confirm a specific timing on
     * the day of the job request. The update is then updated to the database and a notification sent
     * to the user who made the job request.
     * @param request The request to be confirmed.
     * @param requestId The id of the request to be confirmed.
     */
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

    /**
     * Marks a job as finished.
     * @param request The request to mark as finished.
     * @param requestId The id of the request to mark as finished.
     */
    private void markJobAsFinished(JobRequests request, String requestId) {
        CollectionReference collection = FirebaseFirestore.getInstance()
                .collection(JobRequests.DATABASE_COLLECTION);
        Map<String, Object> updates = new HashMap<>();
        updates.put(JobRequests.FIELD_STATUS, JobRequests.STATUS_FINISHED);
        collection.document(requestId).update(updates);
        NotificationHandler.requestFinished(request);
        Toast.makeText(getActivity(), "Job request marked as finished", Toast.LENGTH_SHORT).show();
    }

}