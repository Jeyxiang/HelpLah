package com.example.helplah.viewmodel.consumer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.helplah.R;
import com.example.helplah.viewmodel.login_screen.LoginScreen;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigationBar;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.navigationBar = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.consumerContainerView);
        this.navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(this.navigationBar, this.navController);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser == null) {
            Intent goToLoginScreen = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(goToLoginScreen);
        }

    }
}