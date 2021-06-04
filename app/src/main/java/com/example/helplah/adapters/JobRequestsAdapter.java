package com.example.helplah.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.models.JobRequests;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JobRequestsAdapter extends FirestoreRecyclerAdapter<JobRequests, JobRequestsAdapter.RequestsViewHolder> {

    private static final String TAG = "Job requests adapter";

    public interface RequestClickedListener {

        void onRequestClicked(JobRequests requests);

        boolean onRequestLongClicked(JobRequests requests);
    }

    private RequestClickedListener mListener;
    private Context context;
    private boolean isBusiness;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public JobRequestsAdapter(@NonNull FirestoreRecyclerOptions<JobRequests> options,
                              RequestClickedListener listener, boolean isBusiness) {
        super(options);
        this.mListener = listener;
        this.isBusiness = isBusiness;
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull JobRequests model) {
        holder.bind(model, this.mListener);
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

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.requestCardView = itemView.findViewById(R.id.requestCardView);
            this.requestName = itemView.findViewById(R.id.requestName);
            this.requestDate = itemView.findViewById(R.id.requestDate);
            this.requestTime = itemView.findViewById(R.id.requestTiming);
            this.requestDescription = itemView.findViewById(R.id.requestDescription);
        }

        public void bind(final JobRequests request, final RequestClickedListener listener) {

            Log.d(TAG, "bind: " + request.getStatus());
            // set color of cardView based on status of job request
            if (request.getStatus() == JobRequests.STATUS_PENDING) {
                this.requestCardView.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.jobRequestPending));
            } else if (request.getStatus() == JobRequests.STATUS_CONFIRMED) {
                this.requestCardView.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.jobRequestAccepted));
            } else {
                this.requestCardView.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.jobRequestCancelled));
            }

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

            itemView.setOnClickListener(x -> listener.onRequestClicked(request));
            itemView.setOnLongClickListener(x -> listener.onRequestLongClicked(request));
        }
    }
}
