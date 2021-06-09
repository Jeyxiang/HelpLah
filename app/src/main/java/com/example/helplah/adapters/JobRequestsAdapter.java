package com.example.helplah.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.models.JobRequests;
import com.example.helplah.models.Listings;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class JobRequestsAdapter extends FirestoreRecyclerAdapter<JobRequests, JobRequestsAdapter.RequestsViewHolder>
    implements ActionMode.Callback {

    private static final String TAG = "Job requests adapter";

    public interface RequestClickedListener {

        void onChatClicked();

        void deleteSelection(ArrayList<String> arrayList);

        void configureSupportAction(ActionMode.Callback callback);

        void actionTwoClicked(View v, JobRequests requests, String requestId);

        void actionOneClicked(JobRequests request, String documentId);
    }

    private final RequestClickedListener mListener;
    private final RecyclerView rv;
    private int mExpandedPosition = RecyclerView.NO_POSITION;
    private boolean multiSelect = false;
    private int numberOfSelected = 0;
    private ArrayList<String> selectedItems = new ArrayList<>();
    private ActionMode actionMode;
    private Context context;
    private final boolean isBusiness;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public JobRequestsAdapter(@NonNull FirestoreRecyclerOptions<JobRequests> options,
                              RequestClickedListener listener, boolean isBusiness, RecyclerView rv) {
        super(options);
        this.mListener = listener;
        this.isBusiness = isBusiness;
        this.rv = rv;
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull JobRequests model) {

        LinearLayout layout = holder.itemView.findViewById(R.id.expandedLayout);
        boolean isExpanded = position == mExpandedPosition;
        layout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        layout.setActivated(isExpanded);
        holder.itemView.setActivated(isExpanded);
        String documentId = getSnapshots().getSnapshot(position).getId();
        holder.bind(model, documentId);

        holder.itemView.setOnClickListener(v -> {
            if (this.multiSelect) {
                selectRequest(holder, documentId);
                return;
            }
            mExpandedPosition = isExpanded ? -1 : position;
            notifyItemChanged(position);
            if (position + 1 == getItemCount()) {
                this.rv.smoothScrollToPosition(position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!this.multiSelect) {
                this.multiSelect = true;
                this.mListener.configureSupportAction(JobRequestsAdapter.this);
                selectRequest(holder, documentId);
            }
            return true;
        });
    }

    private void selectRequest(RequestsViewHolder holder, String documentId) {
        CardView card = holder.itemView.findViewById(R.id.requestCardView);
        if (this.selectedItems.contains(documentId)) {
            this.selectedItems.remove(documentId);
            this.numberOfSelected--;
            card.setAlpha(1.0f);
        } else {
            this.selectedItems.add(documentId);
            this.numberOfSelected++;
            card.setAlpha(0.3f);
        }
        if (this.actionMode != null) {
            if (this.numberOfSelected == 0) {
                onDestroyActionMode(this.actionMode);
            } else {
                actionMode.setTitle(this.numberOfSelected + " selected");
            }
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        this.actionMode = mode;
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.job_request_contextual_action_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.job_request_action_bar_delete) {
            Log.d(TAG, "onActionItemClicked: Deleting items");
            deleteSelectedItems();
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.multiSelect = false;
        this.numberOfSelected = 0;
        this.selectedItems.clear();
        mode.finish();
        notifyDataSetChanged();
    }

    private void deleteSelectedItems() {
        new MaterialAlertDialogBuilder(this.context)
                .setTitle("Are you sure you want to remove selected requests?")
                .setMessage("All uncancelled requests will be cancelled and removed. This" +
                        " action is irreversible.")
                .setPositiveButton("Delete", ((dialog, which) -> {
                    this.mListener.deleteSelection(this.selectedItems);
                    onDestroyActionMode(actionMode);
                }))
                .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                .show();
    }


    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new RequestsViewHolder(inflater.inflate(R.layout.job_request_list_item, parent, false));
    }

    public class RequestsViewHolder extends RecyclerView.ViewHolder {

        private final CardView requestCardView;
        private final TextView requestName;
        private final TextView requestDate;
        private final TextView requestTime;
        private final TextView requestDescription;
        private final TextView requestTimingNote;
        private final CircleImageView image;
        private final ExtendedFloatingActionButton actionOneButton;
        private final ExtendedFloatingActionButton actionTwoButton;
        private final ExtendedFloatingActionButton chatButton;

        public RequestsViewHolder(@NonNull View itemView) {

            super(itemView);
            this.requestCardView = itemView.findViewById(R.id.requestCardView);
            this.requestName = itemView.findViewById(R.id.requestName);
            this.requestDate = itemView.findViewById(R.id.requestDate);
            this.requestTime = itemView.findViewById(R.id.requestTiming);
            this.requestDescription = itemView.findViewById(R.id.requestDescription);
            this.requestTimingNote = itemView.findViewById(R.id.requestTimingNote);
            this.actionOneButton = itemView.findViewById(R.id.requestCancelButton);
            this.actionTwoButton = itemView.findViewById(R.id.requestEditButton);
            this.chatButton = itemView.findViewById(R.id.requestChatButton);
            this.image = itemView.findViewById(R.id.requestImage);
        }

        public void bind(final JobRequests request, final String documentId) {

            if (selectedItems.contains(documentId)) {
                this.requestCardView.setAlpha(0.3f);
            } else {
                this.requestCardView.setAlpha(1f);
            }

            // set color of cardView based on status of job request
            this.requestCardView.setCardBackgroundColor(getColor(request));

            if (isBusiness) {
                this.requestName.setText(request.getCustomerName());
                this.actionTwoButton.setText("Confirm");
                this.actionOneButton.setText("Decline");
            } else {
                this.requestName.setText(request.getBusinessName());
                this.image.setOnClickListener(x -> goToListing(request));
            }

            this.requestDescription.setText(request.getJobDescription());

            this.requestDate.setText(JobRequests.dateToString(request.getDateOfJob()));
            String time = request.getConfirmedTiming();
            this.requestTime.setText(time == null ? "To be confirmed" : time);
            this.requestTimingNote.setText(request.getTimingNote());

            configureActionTwo(request, documentId);
            configureActionOne(request, documentId);
        }

        private int getColor(JobRequests request) {
            if (request.getStatus() == JobRequests.STATUS_PENDING) {
                return ContextCompat.getColor(context, R.color.jobRequestPending);
            } else if (request.getStatus() == JobRequests.STATUS_CONFIRMED) {
                return ContextCompat.getColor(context, R.color.jobRequestAccepted);
            } else {
                return ContextCompat.getColor(context, R.color.jobRequestCancelled);
            }
        }

        private void goToListing(JobRequests request) {
            String businessId = request.getBusinessId();
            CollectionReference listingsDb = FirebaseFirestore.getInstance().collection(Listings.DATABASE_COLLECTION);
            Bundle bundle = new Bundle();

            listingsDb.whereEqualTo(FieldPath.documentId(), businessId).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Listings listing = snapshot.toObject(Listings.class);
                            bundle.putParcelable("listing", listing);
                            Navigation.findNavController(itemView)
                                    .navigate(R.id.action_jobRequests_to_listingDescription, bundle);
                        }
                    });
        }

        private void configureActionOne(JobRequests request, String documentId) {
            this.actionOneButton.setOnClickListener(x -> {
                if (request.getStatus() != JobRequests.STATUS_CANCELLED) {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle(isBusiness
                                    ? "Are you sure you want to decline this job request?"
                                    : "Are you sure you want to cancel this job request")
                            .setPositiveButton(isBusiness ? "Decline request" : "Cancel request",
                                    (dialog, which) -> {
                                        mListener.actionOneClicked(request, documentId);
                                        notifyItemChanged(getBindingAdapterPosition());
                                    })
                            .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    Log.d(TAG, "cancelClicked: cancelled already");
                    Toast.makeText(context, "Request has already been cancelled", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void configureActionTwo(JobRequests request, String documentId) {
            this.actionTwoButton.setOnClickListener(v -> {
                if (request.getStatus() == JobRequests.STATUS_CANCELLED) {
                    Toast.makeText(context, "Request has been cancelled",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mListener.actionTwoClicked(v, request, documentId);
                notifyItemChanged(getBindingAdapterPosition());
            });
        }

    }
}
