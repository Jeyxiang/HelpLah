package com.example.helplah.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A class that abstracts a review for a job request.
 */
public class Review implements Parcelable {

    private static final String TAG = "Reviews";
    public static final String DATABASE_COLLECTION = "Reviews";

    public static final String FIELD_REVIEW_TEXT = "reviewText";
    public static final String FIELD_REPLY_TEXT = "reply";
    public static final String FIELD_DATE_REPLY = "dateReplied";
    public static final String FIELD_DATE_REVIEWED = "dateReviewed";
    public static final String FIELD_SCORE = "score";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_SERVICE = "service";
    public static final String FIELD_BUSINESS_ID = "businessId";
    public static final String FIELD_REVIEW_ID = "reviewId";


    // The review text.
    private String reviewText;
    // The date when the review was created.
    private Date dateReviewed;
    // The reply to the review made bu the business. Null at the start.
    private String reply;
    // The date of the reply.
    private Date dateReplied;
    // The score of the review.
    private float score;
    // The service provided by the business for the review.
    private String service;
    private String username;
    private String userId;
    private String jobRequestId;
    private String businessName;
    private String businessId;
    private String reviewId;

    public Review() {}

    public Review(String username, String userId, String businessName, String businessId) {
        this.username = username;
        this.userId = userId;
        this.businessName = businessName;
        this.businessId = businessId;
    }

    protected Review(Parcel in) {
        reviewText = in.readString();
        reply = in.readString();
        score = in.readFloat();
        service = in.readString();
        username = in.readString();
        userId = in.readString();
        businessName = in.readString();
        businessId = in.readString();
        reviewId = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reviewText);
        dest.writeString(reply);
        dest.writeFloat(score);
        dest.writeString(service);
        dest.writeString(username);
        dest.writeString(userId);
        dest.writeString(businessName);
        dest.writeString(businessId);
        dest.writeString(reviewId);
    }

    /**
     * Users the information from a job request to initialise a review.
     * @param request The job request to elave a review on.
     * @return The review.
     */
    public static Review jobRequestToReview(JobRequests request) {
        Review review = new Review(request.getCustomerName(), request.getCustomerId(),
                request.getBusinessName(), request.getBusinessId());
        review.setService(request.getService());
        review.setDateReviewed(new Date(System.currentTimeMillis()));
        return review;
    }

    /**
     * Takes a date and returns a string that represents how long ago the date was.
     * @param past The date.
     * @return How long ago the date was.
     */
    public static String getTimeAgo(Date past) {
        if (past == null) {
            return "";
        }

        Date now = new Date();
        long timeDifference = now.getTime() - past.getTime();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference);
        long hours = TimeUnit.MILLISECONDS.toHours(timeDifference);
        long days = TimeUnit.MILLISECONDS.toDays(timeDifference);

        if (seconds < 60) {
            return seconds + " seconds ago";
        } else if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else if (days < 25) {
            return days + " days ago";
        } else {
            DateFormat formatter = new SimpleDateFormat("dd MMM");
            return formatter.format(past);
        }
    }

    /**
     * Submit a review by uploading it to the appropriate location in the firestore database.
     * @param review The review to be submitted.
     * @param context The context where this function was called from.
     */
    public static void submitReview(Review review, FragmentActivity context) {

        CollectionReference listingReviews = FirebaseFirestore.getInstance()
                .collection(Listings.DATABASE_COLLECTION)
                .document(review.getBusinessId())
                .collection(Review.DATABASE_COLLECTION);

        if (!checkReview(review)) {
            Log.d(TAG, "submitReview: Error while submitting review.");
            Toast.makeText(context, "Failed to submit review. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        listingReviews.add(review).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "submitReview: Review added for " + review.getBusinessName());
            JobRequests.markAsReviewed(review.getJobRequestId());

            String id = documentReference.getId();
            listingReviews.document(id).update(FIELD_REVIEW_ID, id);
            review.setReviewId(id);
            NotificationHandler.LeftAReview(review);

            Toast.makeText(context, "Review submitted", Toast.LENGTH_SHORT).show();
            context.onBackPressed();
        });
    }

    /**
     * Checks if the review object has all the necessary fields before uploading it to the database.
     * @param review The review to be uploaded to the database.
     * @return True if it is safe to upload to the database, false otherwise.
     */
    public static boolean checkReview(Review review) {
        return review.getReviewText() != null && review.getBusinessName() != null &&
                review.getUsername() != null && review.getBusinessId() != null &&
                review.getJobRequestId() != null && review.getUserId() != null &&
                review.getScore() != 0.0 && review.getDateReviewed() != null &&
                review.getService() != null;
    }

    /**
     * Edit a review by updating it to the appropriate location in the firestore database.
     * @param review The review to be edited.
     * @param context The context where this function was called from.
     */
    public static void editReview(Review review, FragmentActivity context) {
        CollectionReference listingReviews = FirebaseFirestore.getInstance()
                .collection(Listings.DATABASE_COLLECTION)
                .document(review.getBusinessId())
                .collection(Review.DATABASE_COLLECTION);

        Map<String, Object> changes = new HashMap<>();
        changes.put(Review.FIELD_SCORE, review.getScore());
        changes.put(Review.FIELD_REVIEW_TEXT, review.getReviewText());
        listingReviews.document(review.getReviewId()).update(changes)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Review editted", Toast.LENGTH_SHORT).show();
                    context.onBackPressed();
                });
    }

    /**
     * Reply to a review by updating the review in the firestore database and adding in the reply.
     * @param review The review to be replied.
     * @param context The context where this function was called from.
     * @param isEdit Is this reply new or an edited reply.
     */
    public static void replyReview(Review review, FragmentActivity context, boolean isEdit) {

        CollectionReference listingReviews = FirebaseFirestore.getInstance()
                .collection(Listings.DATABASE_COLLECTION)
                .document(review.getBusinessId())
                .collection(Review.DATABASE_COLLECTION);

        listingReviews.document(review.getReviewId())
                .update(FIELD_REPLY_TEXT, review.getReply(), FIELD_DATE_REPLY, new Date())
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Replied to review", Toast.LENGTH_SHORT).show();
                    review.setDateReplied(new Date());
                    if (isEdit) {
                        NotificationHandler.repliedReview(review);
                    }
                    context.onBackPressed();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to reply. Please try again", Toast.LENGTH_SHORT).show();
                });
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Date getDateReviewed() {
        return dateReviewed;
    }

    public void setDateReviewed(Date dateReviewed) {
        this.dateReviewed = dateReviewed;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Date getDateReplied() {
        return dateReplied;
    }

    public void setDateReplied(Date dateReplied) {
        this.dateReplied = dateReplied;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getJobRequestId() {
        return jobRequestId;
    }

    public void setJobRequestId(String jobRequestId) {
        this.jobRequestId = jobRequestId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
}
