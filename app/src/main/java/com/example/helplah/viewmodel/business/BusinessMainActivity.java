package com.example.helplah.viewmodel.business;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.helplah.R;
import com.example.helplah.models.FCMHandler;
import com.example.helplah.models.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

/***
 * The main activity of the business interface which hosts all the fragments and navigation component.
 */
public class BusinessMainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_main);

        BottomNavigationView navigationBar = findViewById(R.id.bizBottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.businessContainerView);

        this.navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(navigationBar, this.navController);

        navigationBar.setOnNavigationItemReselectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.businessJobsRequestsFragment:
                    navController.popBackStack(R.id.businessJobsRequestsFragment, false);
                case R.id.businessChatFragment:
                    navController.popBackStack(R.id.businessChatFragment, false);
                case R.id.businessAccountFragment:
                    navController.popBackStack(R.id.businessAccountFragment, false);
            }
        });
        updateToken();
        //navigate to page upon clicking of notification
        boolean gotoNotification = getIntent().getBooleanExtra("gotoNotification",false);
        boolean gotoChat = getIntent().getBooleanExtra("gotoChat",false);
        if (gotoNotification) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("gotoNotification",true);
            Log.d("Normal Notification result","directing to account fragment");
            navController.navigate(R.id.businessAccountFragment, bundle);
        } else if (gotoChat) {
            Log.d("Comet Notification result","directing to chat fragment");
            navController.navigate(R.id.businessChatFragment);
        }
    }

    private void updateToken(){
        //update the token of the device into the firestore
        FCMHandler.enableFCM();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Business Main Activity", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String refreshToken = task.getResult();
                        Token token= new Token(refreshToken);
                        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseFirestore.getInstance().collection(Token.DATABASE_COLLECTION).document(userid).set(token);

                    }
                });
    }
}