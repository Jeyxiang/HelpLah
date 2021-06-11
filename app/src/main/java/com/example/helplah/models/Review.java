package com.example.helplah.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Review implements Parcelable {

    public static final String DATABASE_COLLECTION = "Reviews";

    public static final String FIELD_REVIEW_TEXT =  "reviewText";
    public static final String FIELD_DATE_REVIEWED = "dateReviewed";
    public static final String FIELD_SCORE = "score";

    private String reviewText;
    private Date dateReviewed;
    private String reply;
    private Date dateReplied;
    private float score;
    private String service;
    private String username;
    private String userId;
    private String jobRequestId;
    private String businessName;
    private String businessId;

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
    }

    // Uses the information from a job request to initialise a review
    public static Review jobRequestToReview(JobRequests request) {
        Review review = new Review(request.getCustomerName(), request.getCustomerId(),
                request.getBusinessName(), request.getCustomerId());
        review.setService(request.getService());
        return review;
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
}
