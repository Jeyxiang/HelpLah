package com.example.helplah.models;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

public class FCMHandler {

    public static void enableFCM(){
        // Enable FCM via enable Auto-init service which generates a new token
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    public static void disableFCM(){
        // Disable auto init
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);
        new Thread(() -> {
            try {
                // Remove registered token from the server
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
