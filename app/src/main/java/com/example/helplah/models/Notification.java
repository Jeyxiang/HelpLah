package com.example.helplah.models;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.example.helplah.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

/**
 * A class that extracts a notification.
 */
public class Notification {

    private static final String TAG = "Notifications model";
    public static final String DATABASE_COLLECTION = "Notifications";

    // The name of the field in the firestore database.
    // Use these static fields when updating or adding to the firestore database.
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_TEXT = "text";
    public static final String FIELD_ACTION_ID = "actionId";
    public static final String FIELD_DATE = "date";

    // The different types of notification
    public static final int JOB_REQUEST_CREATED = 1;
    public static final int JOB_REQUEST_EDITED = 2;
    public static final int JOB_REQUEST_CANCELLED = 3;
    public static final int JOB_REQUEST_CONFIRMED = 4;
    public static final int JOB_REQUEST_FINISHED = 5;
    public static final int LEFT_A_REVIEW = 6;
    public static final int REPLIED_REVIEW = 7;

    // The type of the notification.
    private int type;
    // The ate of the notification.
    private Date date;
    // The title of the notification.
    private String title;
    // A description about the notification.
    private String text;
    // The id related to the item of the notification. For example, if the notification is regarding
    // a job request, then the actionId stores the firestore id of the job request.
    private String actionId;
    // The user id of the recipient.
    private String recipientId;
    // The user id of the sender.
    private String senderId;

    public Notification() {}

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }


    /**
     * Performs an action when a notification is clicked depending on the type of the notification
     * and who clicked the notification.
     * @param notification The notification clicked.
     * @param v The view where the notification was clicked at.
     * @param isBusiness Is the notification clicked by a business or consumer user.
     * @param context The context in which the notification was clicked.
     */
    public static void notificationClicked(Notification notification, View v,
                                           boolean isBusiness, Context context) {
        if (notification.getType() >= Notification.JOB_REQUEST_CREATED
                && notification.getType() <= Notification.JOB_REQUEST_FINISHED) {
            FirebaseFirestore.getInstance()
                    .collection(JobRequests.DATABASE_COLLECTION)
                    .document(notification.getActionId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        JobRequests request = documentSnapshot.toObject(JobRequests.class);
                        if (request == null) {
                            Toast.makeText(context,
                                    "This request has already been deleted by the user.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("id", request.getId());
                        if (isBusiness) {
                            Navigation.findNavController(v)
                                    .navigate(R.id.action_businessAccountFragment_to_businessJobRequestNotification, bundle);
                        } else {
                            Navigation.findNavController(v)
                                    .navigate(R.id.action_accountFragment_to_userJobRequestNotification, bundle);
                        }
                    });
        } else if (notification.getType() == Notification.LEFT_A_REVIEW && isBusiness) {
            FirebaseFirestore.getInstance().collection(Listings.DATABASE_COLLECTION)
                    .document(notification.getRecipientId())
                    .collection(Review.DATABASE_COLLECTION)
                    .document(notification.getActionId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Review review = documentSnapshot.toObject(Review.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("review", review);
                        Navigation.findNavController(v)
                                .navigate(R.id.action_businessAccountFragment_to_replyReviewFragment,
                                        bundle);
                    });
        } else if (notification.getType() == Notification.REPLIED_REVIEW && !isBusiness) {
            FirebaseFirestore.getInstance().collection(Listings.DATABASE_COLLECTION)
                    .document(notification.getSenderId())
                    .collection(Review.DATABASE_COLLECTION)
                    .document(notification.getActionId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Review review = documentSnapshot.toObject(Review.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Review.DATABASE_COLLECTION, review);
                        Navigation.findNavController(v)
                                .navigate(R.id.action_accountFragment_to_viewReplyFragment, bundle);
                    });
        }
    }
}
