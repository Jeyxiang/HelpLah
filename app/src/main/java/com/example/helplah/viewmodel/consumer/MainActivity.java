package com.example.helplah.viewmodel.consumer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.example.helplah.R;
import com.example.helplah.models.Constants;
import com.example.helplah.models.User;
import com.example.helplah.viewmodel.business.BusinessMainActivity;
import com.example.helplah.viewmodel.login_screen.LoginScreen;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main activity";

    private String userId;
    private String userName;
    private BottomNavigationView navigationBar;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "onCreate: " + mUser);

        if (mUser == null) {
            Intent goToLoginScreen = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(goToLoginScreen);
            return;
        }

        this.userId = mUser.getUid();


        CollectionReference dbUsers = FirebaseFirestore.getInstance().collection(User.DATABASE_COLLECTION);

        dbUsers.whereEqualTo(FieldPath.documentId(), this.userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                User user = snapshot.toObject(User.class);
                userName = user.getUsername();
                if (user.isBusiness()) {
                    configureCometChat();
                    Intent goToBiz = new Intent(getApplicationContext(), BusinessMainActivity.class);
                    startActivity(goToBiz);
                    this.finish();
                    return;
                } else{
                    configureCometChat();
                    goToConsumer();
                }
            }
        });

    }

    private void goToConsumer() {
        this.navigationBar = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.consumerContainerView);
        this.navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(this.navigationBar, this.navController);

        this.navigationBar.setOnNavigationItemReselectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.servicesCategoriesFragment:
                    navController.popBackStack(R.id.servicesCategoriesFragment, false);
                case R.id.jobRequestsFragment:
                    navController.popBackStack(R.id.jobRequestsFragment, false);
                case R.id.chatFragment:
                    navController.popBackStack(R.id.chatFragment, false);
                case R.id.accountFragment:
                    navController.popBackStack(R.id.accountFragment, false);
            }
        });
    }

    private void configureCometChat() {
        if (CometChat.getLoggedInUser() == null) {
            cometChatLogin();
            return;
        }
        String cometId = CometChat.getLoggedInUser().getUid();
        if (!cometId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toLowerCase())) {
            cometChatLogin();
        }
    }

    private void cometChatLogin() {
        CometChat.login(this.userId, Constants.COMET_CHAT_AUTH_KEY,
                new CometChat.CallbackListener<com.cometchat.pro.models.User>() {
            @Override
            public void onSuccess(com.cometchat.pro.models.User user) {
                Log.d(TAG, "onSuccess: Signed in successfully " + user.toString());
                registerFCMToken();
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: No user found " + e.getMessage());
                cometCreateUser();
            }
        });
    }

    private void cometCreateUser() {
        com.cometchat.pro.models.User user = new com.cometchat.pro.models.User();
        user.setUid(this.userId);
        user.setName(this.userName);
        CometChat.createUser(user, Constants.COMET_CHAT_AUTH_KEY,
                new CometChat.CallbackListener<com.cometchat.pro.models.User>() {
            @Override
            public void onSuccess(com.cometchat.pro.models.User user) {
                cometChatLogin();
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: Failed to log in or create user. Try again " + e.getMessage());
            }
        });
    }

    private void registerFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            CometChat.registerTokenForPushNotification(task.getResult(), new CometChat.CallbackListener<String>() {
                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "onSuccess: Registered for push notifications with token " + task.getResult());
                }

                @Override
                public void onError(CometChatException e) {
                    Log.d(TAG, "onError: Failed to register for push notifications: " + e.getMessage());
                }
            });
        });
    }
}