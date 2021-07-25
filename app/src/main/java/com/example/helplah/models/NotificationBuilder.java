package com.example.helplah.models;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.helplah.R;
import com.example.helplah.viewmodel.business.BusinessMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationBuilder extends FirebaseMessagingService {

    private String id = "channel_001";
    private String name = "name";
    private boolean isBusiness;

    public NotificationBuilder() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        Log.d("Notification","OnMessageReceived: Message is received");
        String title = remoteMessage.getData().get("Title");
        String message = remoteMessage.getData().get("Message");
        Log.d("Notification", "onMessageReceived: " + title);
        Log.d("Notification", "onMessageReceived: " + message);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //user will not receive notification when signed out
        if (user != null && title != null && message != null) {
            sendNormalNotification(title, message);
        } else {
            sendComet();
        }
    }



    public void sendComet() {
        PendingIntent pendingIntent;
        String title = "Comet Chat";
        String message = "You have a new Message!";

        if (isBusiness()) {
            Log.d("comet Notification","Business intent created");
            Intent myIntent = new Intent(getApplicationContext(), BusinessMainActivity.class);
            myIntent.putExtra("gotoChat",true);
            pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    myIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        } else {
            Log.d("comet Notification","User intent created");
            pendingIntent = new NavDeepLinkBuilder(getApplicationContext())
                    .setGraph(R.navigation.consumer_navigation)
                    .setDestination(R.id.chatFragment)
                    .createPendingIntent();
        }
        pingNotification(pendingIntent,title,message);
    }

    private void sendNormalNotification(String title,String message) {
        PendingIntent pendingIntent;

        if (isBusiness()) {
            Intent myIntent = new Intent(getApplicationContext(), BusinessMainActivity.class);
            myIntent.putExtra("gotoNotification",true);
            pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    myIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("gotoNotification",true);
            pendingIntent = new NavDeepLinkBuilder(getApplicationContext())
                    .setGraph(R.navigation.consumer_navigation)
                    .setDestination(R.id.accountFragment)
                    .setArguments(bundle)
                    .createPendingIntent();
        }
        pingNotification(pendingIntent,title,message);
    }

    private int spawnID() {
        int min = 1;
        int max = 999;
        return (int) Math.floor(Math.random()*(max-min+1)+min);
    }

    private boolean isBusiness() {
        String TAG = "Check if Business";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        DocumentReference doc = FirebaseFirestore.getInstance()
                .collection(User.DATABASE_COLLECTION).document(userid);
        doc.get().addOnSuccessListener(documentSnapshot -> {
            User userClass = documentSnapshot.toObject(User.class);
            isBusiness = userClass.isBusiness();
            Log.d(TAG,"Receiver is Business: " + isBusiness);
        });
        return isBusiness;
    }



    private void pingNotification(PendingIntent pendingIntent,String title,String message) {
        //Each notification ID will be different in order to spawn multiple notifications
        int value = spawnID();
        Notification notification = null;
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(NOTIFICATION_SERVICE);
        //build channel which is required for builds above Android SDK 26++
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//Judge API
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            notification = new NotificationCompat.Builder(this)
                    .setChannelId(id)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLights(Color.GREEN, 1000, 1000)//Set three color lights
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .build();
            Log.d("CHANNEL","Notification sent w Channel");
        } else {
            notification = new NotificationCompat.Builder(this)
                    .setChannelId(id)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setOngoing(true)
                    .setLights(Color.GREEN, 1000, 1000)//Set three color lights
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .build();//invalid
            Log.d("CHANNEL","Notification sent w/o Channel");
        }
        notificationManager.notify(value, notification);
        Log.d("CHANNEL","Notification sent");
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        if(firebaseUser!=null){
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Token token1= new Token(refreshToken);
        FirebaseFirestore.getInstance().collection(Token.DATABASE_COLLECTION).document(firebaseUser.getUid()).set(token1);
    }
}
