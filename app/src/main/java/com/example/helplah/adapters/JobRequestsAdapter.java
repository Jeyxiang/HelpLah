package com.example.helplah.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class JobRequestsAdapter extends FirestoreRecyclerAdapter<JobRequests, JobRequestsAdapter.RequestsViewHolder> {

    private static final String TAG = "Job requests adapter";

    public interface RequestClickedListener {

        void onChatClicked();

        void actionTwoClicked(View v, JobRequests requests, String requestId);

        void actionOneClicked(JobRequests request, String documentId);
    }
    private final RequestClickedListener mListener;
    private final RecyclerView rv;
    private int mExpandedPosition = RecyclerView.NO_POSITION;
    private Context context;
    private boolean isBusiness;

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

        holder.itemView.setOnClickListener(v -> {
            mExpandedPosition = isExpanded ? -1 : position;
            notifyItemChanged(position);
            if (position + 1 == getItemCount()) {
                this.rv.smoothScrollToPosition(position);
            }
        });

        String documentId = getSnapshots().getSnapshot(position).getId();
        holder.bind(model, this.mListener, documentId);
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

        public void bind(final JobRequests request, final RequestClickedListener listener,
                         final String documentId) {

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

            this.actionTwoButton.setOnClickListener(v -> {
                if (request.getStatus() == JobRequests.STATUS_CANCELLED) {
                    Toast.makeText(context, "Request has been cancelled",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mListener.actionTwoClicked(v, request, documentId);
                notifyItemChanged(getBindingAdapterPosition());
            });

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

    }
}
