package com.example.helplah.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.helplah.APIs.NotificationAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class which contains methods to create new notifications and adding it to a database. The recipient
 * and sender of the notification is set via the setRecipientId and setSenderId methods.
 */
public class NotificationHandler {

    /**
     * Creates a notification when a request is created and uploads it to the database.
     * @param request The job request created.
     */
    public static void requestCreated(JobRequests request) {
        Notification notification = new Notification();
        notification.setType(Notification.JOB_REQUEST_CREATED);
        String title = request.getCustomerName() + " sent a job request.";
        String text = "Please confirm or decline this job request.";
        notification.setDate(new Date());
        notification.setText(text);
        notification.setTitle(title);
        notification.setActionId(request.getId());
        notification.setRecipientId(request.getBusinessId());
        notification.setSenderId(request.getCustomerId());
        sendNotification(notification);
    }

    /**
     * Creates a notification when a user edits and changes the job request.
     * @param request The job request edited.
     */
    public static void requestChanged(JobRequests request) {
        if (request.getStatus() == JobRequests.STATUS_CONFIRMED) {
            Notification notification = new Notification();
            notification.setType(Notification.JOB_REQUEST_EDITED);
            String title = request.getCustomerName() + " changed a confirmed job request.";
            String text = "Please view the changes and then re-confirm or cancel the edited request.";
            notification.setDate(new Date());
            notification.setText(text);
            notification.setTitle(title);
            notification.setActionId(request.getId());
            notification.setRecipientId(request.getBusinessId());
            notification.setSenderId(request.getCustomerId());
            sendNotification(notification);
        }
    }

    /**
     * Creates a notification if a job request has been cancelled.
     * @param request The job request that was cancelled.
     * @param cancelledByUser If the job request was cancelled by the user or declined by the business.
     */
    public static void requestCancelled(JobRequests request, boolean cancelledByUser) {
        Notification notification = new Notification();
        notification.setType(Notification.JOB_REQUEST_CANCELLED);
        String title;
        String text;
        String recipientId;
        String senderId;
        if (cancelledByUser) {
            title = request.getCustomerName() + " cancelled a job request.";
            text = "Job request has been cancelled.";
            recipientId = request.getBusinessId();
            senderId = request.getCustomerId();
        } else {
            title = request.getBusinessName() + " cancelled your job request.";
            text = "Reason for cancellation: (" + request.getDeclineMessage() + ") " +
                    "Send a new request if needed.";
            recipientId = request.getCustomerId();
            senderId = request.getBusinessId();
        }
        notification.setText(text);
        notification.setTitle(title);
        notification.setDate(new Date());
        notification.setActionId(request.getId());
        notification.setRecipientId(recipientId);
        notification.setSenderId(senderId);
        sendNotification(notification);
    }

    /**
     * Creates a notification when a business confirms a job request.
     * @param request The job request that was confirmed.
     */
    public static void requestConfirmed(JobRequests request) {
        Notification notification = new Notification();
        notification.setType(Notification.JOB_REQUEST_CONFIRMED);
        String title = request.getBusinessName() + " confirmed your job request.";
        String text = "Your job request has been confirmed for " + request.getConfirmedTiming() + ".";
        notification.setText(text);
        notification.setTitle(title);
        notification.setDate(new Date());
        notification.setActionId(request.getId());
        notification.setRecipientId(request.getCustomerId());
        notification.setSenderId(request.getBusinessId());
        sendNotification(notification);
    }

    /**
     * Creates a notification when a business marks a completed job requests as finished.
     * @param request The request that was marked as finished.
     */
    public static void requestFinished(JobRequests request) {
        Notification notification = new Notification();
        notification.setType(Notification.JOB_REQUEST_FINISHED);
        String title = "Your job request with " + request.getBusinessName() + " has been completed.";
        String text = "Please leave a review for " + request.getBusinessName() + ".";
        notification.setText(text);
        notification.setTitle(title);
        notification.setDate(new Date());
        notification.setActionId(request.getId());
        notification.setRecipientId(request.getCustomerId());
        notification.setSenderId(request.getBusinessId());
        sendNotification(notification);
    }

    /**
     * Creates a notification when a user left a review for the business.
     * @param review The review made by the user.
     */
    public static void LeftAReview(Review review) {
        Notification notification = new Notification();
        notification.setType(Notification.LEFT_A_REVIEW);
        String title = review.getUsername() + " has left a review.";
        String text = "Click to view the review.";
        notification.setText(text);
        notification.setTitle(title);
        notification.setDate(new Date());
        notification.setActionId(review.getReviewId());
        notification.setRecipientId(review.getBusinessId());
        notification.setSenderId(review.getUserId());
        sendNotification(notification);
    }

    /**
     * Creates a notification when a business replies to a review made by the user.
     * @param review The review which the business replied.
     */
    public static void repliedReview(Review review) {
        Notification notification = new Notification();
        notification.setType(Notification.REPLIED_REVIEW);
        String title = review.getUsername() + " has replied to your.";
        String text = "Click to view the reply.";
        notification.setText(text);
        notification.setTitle(title);
        notification.setDate(new Date());
        notification.setActionId(review.getReviewId());
        notification.setRecipientId(review.getUserId());
        notification.setSenderId(review.getBusinessId());
        sendNotification(notification);
    }

    /**
     * Takes a notification and uploads it tot he firestore database.
     * @param notification The notification to upload.
     */
    public static void sendNotification(Notification notification) {
        String userId = notification.getRecipientId();
        CollectionReference db = FirebaseFirestore.getInstance()
                .collection(User.DATABASE_COLLECTION)
                .document(userId)
                .collection(Notification.DATABASE_COLLECTION);

        popNotification(notification.getRecipientId(), notification.getTitle(), notification.getText());
        db.add(notification);
    }

    public static void popNotification(String userid, String title, String message) {
        String TAG = "Notification Result";
        Log.d(TAG, "popNotification: Sending notification");
        NotificationAPI apiService = Client.getClient("https://fcm.googleapis.com/").create(NotificationAPI.class);
        DocumentReference userDoc = FirebaseFirestore.getInstance().collection(Token.DATABASE_COLLECTION)
                .document(userid);
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String usertoken = (String) document.get("token");
                        Log.d(TAG, "onComplete: usertoken for " + userid + "is " + usertoken);
                        Data data = new Data(title, message);
                        NotificationData sender = new NotificationData(data, usertoken);
                        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.code() == 200) {
                                    if (response.body().success != 1) {
                                        //fail to send notification
                                        Log.e(TAG, "Fail to send notification");
                                        Log.d(TAG, "onResponse: Failed to sent notification.");
                                    } else {
                                        Log.e(TAG, "notification sent");
                                        Log.d(TAG, "onResponse: Notification send");
                                    }
                                } else {
                                    Log.d(TAG, "onResponse: Respond code not 200" + response);
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Log.d(TAG, "Fail to send notification");
                                Log.d(TAG, "onFailure: Failed to send notification.");
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
