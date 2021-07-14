package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.helplah.R;
import com.example.helplah.models.Review;

/**
 * Allows the user to view a reply to their review after the business had replied to
 * a previously made review.
 *
 * This fragment requires the following arguments in the bundle when navigating to it:
 * Review - Parcelable: The review replied to.
 */
public class ViewReplyFragment extends Fragment {

    private Review review;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.review = getArguments().getParcelable(Review.DATABASE_COLLECTION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_reply, container, false);

        TextView titleName = rootView.findViewById(R.id.replyUserNameTitle);
        TextView reviewText = rootView.findViewById(R.id.replyReviewReviewText);
        TextView replyTitle = rootView.findViewById(R.id.replyBusinessReviewTitle);
        TextView replyText = rootView.findViewById(R.id.replyText);

        titleName.setText(this.review.getBusinessName());
        reviewText.setText(this.review.getReviewText());
        replyTitle.setText(this.review.getBusinessName() + " replied:");
        replyText.setText(this.review.getReply());

        return rootView;
    }
}