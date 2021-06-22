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
import com.example.helplah.models.Review;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class EditReviewFragment extends Fragment {

    private static final String TAG = "Write review fragment";

    private View rootView;
    private JobRequests request;
    private Review review;

    private TextView reviewBusinessName;
    private TextView reviewJobDate;
    private EditText reviewComment;
    private MaterialRatingBar ratingBar;
    private boolean ratingBarClicked;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            assert getArguments() != null;
            this.request = getArguments().getParcelable(JobRequests.DATABASE_COLLECTION);
            this.review = getArguments().getParcelable(Review.DATABASE_COLLECTION);
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

        this.reviewBusinessName.setText(this.review.getBusinessName());
        this.reviewJobDate.setText(JobRequests.dateToString(this.request.getDateOfJob()));
        submitButton.setOnClickListener(v -> editReview());

        this.ratingBar.setOnRatingChangeListener((ratingBar, rating) -> ratingBarClicked = true);
        this.reviewComment.setText(this.review.getReviewText());
        this.ratingBar.setRating(this.review.getScore());

        return this.rootView;
    }

    private void editReview() {
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
        this.review.setScore(score);
        this.review.setReviewText(reviewText);

        Log.d(TAG, "submitReview: " + review.getBusinessId());
        Review.editReview(review, requireActivity());
    }
}
