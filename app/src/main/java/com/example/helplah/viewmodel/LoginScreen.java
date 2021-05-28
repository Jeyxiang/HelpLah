package com.example.helplah.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.helplah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {

    private static final String TAG = "Login Activity";

    private FirebaseAuth mAuth;
    private EditText username;
    private EditText password;
    private CardView loginButton;
    private TextView registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        this.mAuth = FirebaseAuth.getInstance();
        this.username = findViewById(R.id.loginUsername);
        this.password = findViewById(R.id.loginPassword);
        this.loginButton = findViewById(R.id.loginLoginButton);
        this.registerButton = findViewById(R.id.loginRegisterButton);

        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign in
                signIn(v);
            }
        });

        this.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Bring to register page
                Intent intent = new Intent(getApplicationContext(), RegisterScreen.class);
                startActivity(intent);
            }
        });
    }

    private void signIn(View v) {
        mAuth.signInWithEmailAndPassword(this.username.getText().toString(),
                this.password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User signed in");
                            // Got to home page of app
                            Intent intent = new Intent(getApplicationContext(), ServicesCategoriesActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "Sign in failed");
                            Snackbar.make(v, "No such user found", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}