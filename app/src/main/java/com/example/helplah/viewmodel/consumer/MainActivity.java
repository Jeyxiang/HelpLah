package com.example.helplah.viewmodel.consumer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.helplah.R;
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

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigationBar;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser == null) {
            Intent goToLoginScreen = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(goToLoginScreen);
        }

        String id = mUser.getUid();

        CollectionReference dbUsers = FirebaseFirestore.getInstance().collection(User.DATABASE_COLLECTION);

        dbUsers.whereEqualTo(FieldPath.documentId(), id).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                User user = snapshot.toObject(User.class);
                if (user.isBusiness()) {
                    Intent goToBiz = new Intent(getApplicationContext(), BusinessMainActivity.class);
                    startActivity(goToBiz);
                } else{
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
}