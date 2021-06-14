package com.example.helplah.viewmodel.login_screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helplah.APIs.JavaMailAPI;
import com.example.helplah.R;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String userEmail = "";
    private Button resetButton;
    private EditText emailAddress;
    private EditText authCode;
    private int codeValue = 100000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        ImageView backButton = findViewById(R.id.backresetPWButton);
        TextView sendCodeButton = findViewById(R.id.sendCodeButton);
        resetButton = findViewById(R.id.resetPWButton);
        emailAddress = findViewById(R.id.emailAddress);
        authCode = findViewById(R.id.authCode);
        authCode.setEnabled(false);
        resetButton.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(intent);
            }
        });

        sendCodeButton.setOnClickListener(x -> sendCode());

        resetButton.setOnClickListener(x -> resetPassword());

    }
    //Check email format
    private boolean isEmailValid(CharSequence email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        } else {
            Toast.makeText(this, "Please enter a valid Email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //randomly generate a 6 digit number
    private int spawnCode() {
        int min = 100000;
        int max = 999999;
        return (int) Math.floor(Math.random()*(max-min+1)+min);
    }

    private void sendCode() {
        String emailAdd = emailAddress.getText().toString();
        //randomly generate a code
        codeValue = spawnCode();
        if (isEmailValid(emailAdd)) {
            userEmail = emailAdd;
            mAuth.fetchSignInMethodsForEmail(emailAdd).addOnCompleteListener(
                    task -> {
                        boolean notFound = task.getResult().getSignInMethods().isEmpty();
                        if (notFound) {
                            Toast.makeText(this, "Email does not exist!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            authCode.setEnabled(true);
                            resetButton.setEnabled(true);
                            sendEmailCode(emailAdd);
                            Toast.makeText(this, "Please check email for your Authentication code",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(this, "Email does not exist!", Toast.LENGTH_SHORT).show());
        }

    }

    protected void sendEmailCode(String userEmail) {
        String resetCode = "Dear user, your 6-digit Authentication code is " + codeValue + ". If you did not request for " +
                "an update, do contact us immediately.";
        String subjUser = "HelpLah: Reset Password Request.";
        JavaMailAPI emailCode = new JavaMailAPI(this,userEmail, subjUser,resetCode);
        emailCode.execute();
    }

    protected void resetPassword() {
        int enteredCode = Integer.parseInt(authCode.getText().toString().trim());
        if (enteredCode == codeValue) {
            mAuth.sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPassword.this,"New Password sent to " + userEmail,Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ResetPassword.this, userEmail + " does not exist", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this,"wrong input code!",Toast.LENGTH_SHORT).show();
        }
    }
}