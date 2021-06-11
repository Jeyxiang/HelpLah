package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.helplah.R;
import com.example.helplah.models.JobRequests;
import com.example.helplah.models.Listings;
import com.example.helplah.models.Review;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WriteReviewFragment} factory method to
 * create an instance of this fragment.
 */
public class WriteReviewFragment extends Fragment {

    private static final String TAG = "Write review fragment";

    private View rootView;
    private JobRequests request;

    private TextView reviewBusinessName;
    private TextView reviewJobDate;
    private EditText reviewComment;
    private MaterialRatingBar ratingBar;
    private boolean ratingBarClicked;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.request = getArguments().getParcelable(JobRequests.DATABASE_COLLECTION);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: Error" + e.getMessage());
            Toast.makeText(requireActivity(), "An unexpected error occurred", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.write_review_fragment, container, false);

        this.reviewBusinessName = this.rootView.findViewById(R.id.reviewBusinessName);
        this.reviewJobDate = this.rootView.findViewById(R.id.reviewJobDate);
        this.reviewComment = this.rootView.findViewById(R.id.reviewComment);
        this.ratingBar = this.rootView.findViewById(R.id.reviewRatingBar);
        Button submitButton = this.rootView.findViewById(R.id.leaveReviewButton);

        this.reviewBusinessName.setText(this.request.getBusinessName());
        this.reviewJobDate.setText(JobRequests.dateToString(this.request.getDateOfJob()));
        submitButton.setOnClickListener(v -> submitReview());
        this.ratingBar.setOnRatingChangeListener((ratingBar, rating) -> ratingBarClicked = true);

        return this.rootView;
    }

    private void submitReview() {
        if (this.reviewComment.length() == 0) {
            this.reviewComment.setError("Please leave a short comment");
            return;
        }
        if (!ratingBarClicked) {
            Toast.makeText(requireActivity(), "Please leave a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        float score = this.ratingBar.getRating();
        String reviewText = this.reviewComment.getText().toString().trim();
        Review review = Review.jobRequestToReview(this.request);
        review.setJobRequestId(getArguments().getString("requestId"));
        review.setScore(score);
        review.setReviewText(reviewText);

        Log.d(TAG, "submitReview: " + review.getBusinessId());
        CollectionReference listingsCollection = FirebaseFirestore.getInstance()
                .collection(Listings.DATABASE_COLLECTION);
        listingsCollection.document(review.getBusinessId()).collection(Review.DATABASE_COLLECTION)
                .add(review).addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "submitReview: Review added for " + review.getBusinessName());
                    JobRequests.markAsReviewed(review.getJobRequestId());
                    requireActivity().onBackPressed();
                });
    }
}