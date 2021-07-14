package com.example.helplah.models;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.cometchat.pro.models.BaseMessage;
import com.example.helplah.R;
import com.example.helplah.viewmodel.business.BusinessMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class NotificationBuilder extends FirebaseMessagingService {

    private boolean isBusiness;
    private JSONObject json;
    private PendingIntent pendingIntent;
    String id = "channel_001";
    String name = "name";

    public NotificationBuilder() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getData().get("Title");
        String message = remoteMessage.getData().get("Message");
        Log.d("Notification", "onMessageReceived: " + title);
        Log.d("Notification", "onMessageReceived: " + message);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //user will not receive notification when signed out
        if (user != null && title != null && message != null) {
            sendNormalNotification(user, title, message);
        } else {
            sendComet(); //just a placeholder for now
        }
        /*Incomplete, I am not sure how to access the information of the chat content
        title and message should be null if the notification is not originated from the NotificationHandler
            if (title != null && message != null ) {
                Log.e("Normal Notification","first line");
                sendNormalNotification(user, title, message);
            } else {
                try {
                First method
                BaseMessage baseMessage = CometChatHelper.processMessage(new JSONObject(remoteMessage.getData().get("message")));

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"2")
                            .setContentText("Test Content")
                            .setContentTitle("Test Title");

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                    notificationManager.notify(baseMessage.getId(), builder.build());

               Second method
                    Log.e("Comet Notification","first line");
                    JSONObject json = new JSONObject(remoteMessage.getData());
                    Log.e("Comet Notification Service", "JSONObject: "+json.toString());
                    BaseMessage baseMessage = CometChatHelper.processMessage(new JSONObject(remoteMessage.getData().get("text")));
                    sendCometNotification(baseMessage);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
             */
    }



    public void sendComet() {
        Notification notification = null;
        String title = "Comet Chat";
        String message = "You have a new Message!";
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
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .build();
            Log.e("CHANNEL","Notification sent w Channel");
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
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .build();//invalid
            Log.e("CHANNEL","Notification sent w/o Channel");
        }
        notificationManager.notify(1, notification);
    }

    //These 2 methods are Incomplete
    public Bitmap getBitmapFromURL(String strURL) {
        if (strURL!=null) {
            try {
                URL url = new URL(strURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public void sendCometNotification(BaseMessage baseMessage) {
        try {
            Log.e("Comet Notification","function triggered");
            int m = (int) ((new Date().getTime()));
            //String GROUP_ID = "group_id";
            Notification notification = null;
            Notification summaryNotification = null;
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//Judge API
                NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);

                notification = new NotificationCompat.Builder(this, "2")
                        .setChannelId(id)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(json.getString("title"))
                        .setContentText(json.getString("alert"))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        //.setLargeIcon(getBitmapFromURL(baseMessage.getSender().getAvatar()))
                        //.setGroup(GROUP_ID)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .build();

                summaryNotification = new NotificationCompat.Builder(this, "2")
                        .setChannelId(id)
                        .setContentTitle("CometChat")
                        .setContentText("You have new messages")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        //.setGroup(GROUP_ID)
                        //.setGroupSummary(true)
                        .build();
            } else {
                notification = new NotificationCompat.Builder(this, "2")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(json.getString("title"))
                        .setContentText(json.getString("alert"))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        //.setLargeIcon(getBitmapFromURL(baseMessage.getSender().getAvatar()))
                        //.setGroup(GROUP_ID)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .build();

                summaryNotification = new NotificationCompat.Builder(this, "2")
                        .setContentTitle("CometChat")
                        .setContentText("You have new messages")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        //.setGroup(GROUP_ID)
                        //.setGroupSummary(true)
                        .build();
            }
            notificationManager.notify(baseMessage.getId(), notification);
            notificationManager.notify(0, summaryNotification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendNormalNotification(FirebaseUser user,String title,String message) {
        String userid = user.getUid();
        DocumentReference userDoc = FirebaseFirestore.getInstance().collection(User.DATABASE_COLLECTION)
                .document(userid);
        userDoc.addSnapshotListener((value, error) -> {
            User userObject = value.toObject(User.class);
            assert userObject != null;
            isBusiness = userObject.isBusiness();
        });

        //Each notification ID will be different in order to spawn multiple notifications
        int value = spawnID();

        if (isBusiness) {
            Intent myIntent = new Intent(getApplicationContext(), BusinessMainActivity.class);
            myIntent.putExtra("fromNotification",true);
            pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    myIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("fromNotification",true);
            pendingIntent = new NavDeepLinkBuilder(getApplicationContext())
                    .setGraph(R.navigation.consumer_navigation)
                    .setDestination(R.id.accountFragment)
                    .setArguments(bundle)
                    .createPendingIntent();
        }

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
            Log.e("CHANNEL","Notification sent w Channel");
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
            Log.e("CHANNEL","Notification sent w/o Channel");
        }
        notificationManager.notify(value, notification);
    }

    private int spawnID() {
        int min = 1;
        int max = 999;
        return (int) Math.floor(Math.random()*(max-min+1)+min);
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
