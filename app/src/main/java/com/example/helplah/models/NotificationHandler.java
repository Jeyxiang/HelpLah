package com.example.helplah.models;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class NotificationHandler {

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

    public static void requestChanged(JobRequests request) {
        if (request.getStatus() == JobRequests.STATUS_CONFIRMED) {
            Notification notification = new Notification();
            notification.setType(Notification.JOB_REQUEST_EDITED);
            String title = request.getCustomerName() + " changed a job request.";
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

    public static void sendNotification(Notification notification) {
        String userId = notification.getRecipientId();
        CollectionReference db = FirebaseFirestore.getInstance()
                .collection(User.DATABASE_COLLECTION)
                .document(userId)
                .collection(Notification.DATABASE_COLLECTION);

        db.add(notification);
    }
}
