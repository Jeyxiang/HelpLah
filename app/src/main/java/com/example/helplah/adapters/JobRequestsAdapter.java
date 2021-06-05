package com.example.helplah.adapters;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.models.JobRequests;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JobRequestsAdapter extends FirestoreRecyclerAdapter<JobRequests, JobRequestsAdapter.RequestsViewHolder> {

    private static final String TAG = "Job requests adapter";

    public interface RequestClickedListener {

        void onEditClicked(View v, JobRequests requests, String requestId);
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
        private final ExtendedFloatingActionButton cancelButton;
        private final ExtendedFloatingActionButton editButton;
        private final ExtendedFloatingActionButton chatButton;

        public RequestsViewHolder(@NonNull View itemView) {

            super(itemView);
            this.requestCardView = itemView.findViewById(R.id.requestCardView);
            this.requestName = itemView.findViewById(R.id.requestName);
            this.requestDate = itemView.findViewById(R.id.requestDate);
            this.requestTime = itemView.findViewById(R.id.requestTiming);
            this.requestDescription = itemView.findViewById(R.id.requestDescription);
            this.requestTimingNote = itemView.findViewById(R.id.requestTimingNote);
            this.cancelButton = itemView.findViewById(R.id.requestCancelButton);
            this.editButton = itemView.findViewById(R.id.requestEditButton);
            this.chatButton = itemView.findViewById(R.id.requestCancelButton);
        }

        public void bind(final JobRequests request, final RequestClickedListener listener,
                         final String documentId) {

            // set color of cardView based on status of job request
            this.requestCardView.setCardBackgroundColor(getColor(request));

            if (isBusiness) {
                this.requestName.setText(request.getCustomerName());
            } else {
                this.requestName.setText(request.getBusinessName());
            }

            this.requestDescription.setText(request.getJobDescription());

            Date date = request.getDateOfJob();
            DateFormat formatter = new SimpleDateFormat("E, dd MMM");
            this.requestDate.setText(formatter.format(date));
            this.requestTime.setText("To be confirmed");
            this.requestTimingNote.setText(request.getTimingNote());

            this.editButton.setOnClickListener(v -> mListener.onEditClicked(v, request, documentId));
            this.cancelButton.setOnClickListener(x -> cancelClicked(request, documentId));
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

        private void cancelClicked(JobRequests request, String documentId) {

            if (request.getStatus() != JobRequests.STATUS_CANCELLED) {
                request.setStatus(JobRequests.STATUS_CANCELLED);
                notifyItemChanged(getBindingAdapterPosition());
                CollectionReference db = FirebaseFirestore.getInstance().collection(JobRequests.DATABASE_COLLECTION);
                Map<String, Object> status = new HashMap<>();
                status.put(JobRequests.FIELD_STATUS, JobRequests.STATUS_CANCELLED);
                db.document(documentId).update(status);
                Log.d(TAG, "cancelClicked: " + documentId + " status updated");
            } else {
                Log.d(TAG, "cancelClicked: cancelled already");
                Toast.makeText(context, "Item has already been cancelled", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
