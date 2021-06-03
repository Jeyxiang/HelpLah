package com.example.helplah.viewmodel.business;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.helplah.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    }
}