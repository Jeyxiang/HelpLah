package com.example.helplah.viewmodel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helplah.R;

public class RegisterScreen extends AppCompatActivity {

    private Button regAsUser;
    private Button backButton;
    private TextView regAsBiz;
    private EditText mEmail;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mPhoneNumber;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        this.regAsUser = findViewById(R.id.registerButton);
        this.regAsBiz = findViewById(R.id.bizRegisterButton);
        this.backButton = findViewById(R.id.registerBackButton);
        this.mEmail = findViewById(R.id.registerEmail);
        this.mUsername = findViewById(R.id.registerUserName);
        this.mPassword = findViewById(R.id.registerPassword);
        this.mConfirmPassword = findViewById(R.id.registerConfirmPassword);
        this.mPhoneNumber = findViewById(R.id.registerPhoneNumber);

        this.backButton.setOnClickListener(x -> this.finish());
        this.regAsUser.setOnClickListener(x -> userRegister());

//        regAsBiz.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),bizRegActivity.class));
//            }
//        });
    }

    private void userRegister() {
        String email = this.mEmail.getText().toString();
        String username = this.mUsername.getText().toString();
        String password = this.mPassword.getText().toString();
        String confirmPassword = this.mConfirmPassword.getText().toString();
        String phoneNumber = this.mPhoneNumber.getText().toString();
    }
}