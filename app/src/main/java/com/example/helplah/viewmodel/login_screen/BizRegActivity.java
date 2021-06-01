package com.example.helplah.viewmodel.login_screen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helplah.R;
import com.example.helplah.models.Listings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.Objects;
import java.util.function.Supplier;

public class BizRegActivity extends AppCompatActivity {

    private static final String TAG = "Business Registration Activity";
    private FirebaseAuth mAuth;

    private Button backButton;
    private Button nextPage;
    private TextView errorMessage;
    private EditText emailAdd;
    private EditText passWord;
    private EditText repassWord;
    private EditText contactNo;

    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biz_reg);

        mAuth = FirebaseAuth.getInstance();
        this.errorMessage = findViewById(R.id.errorMsg);
        this.emailAdd = findViewById(R.id.registerEmail);
        this.passWord = findViewById(R.id.registerPassword);
        this.repassWord = findViewById(R.id.registerConfirmPassword);
        this.contactNo = findViewById(R.id.registerContactNumber);
        this.nextPage = findViewById(R.id.nextPageButton);
        this.backButton = findViewById(R.id.registerBackButton);

        this.passwordLayout = findViewById(R.id.passwordLayout);
        this.confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        setTextWatcher();

        this.backButton.setOnClickListener(x -> this.finish());
        this.nextPage.setOnClickListener(x -> this.checkIfValid());
    }

    private TextWatcher createTextWatcher(Supplier<Boolean> x, String message, TextInputLayout layout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (x.get()) {
                    layout.setError(message);
                } else {
                    layout.setErrorEnabled(false);
                }
            }
        };
    }

    private void setTextWatcher() {
        Supplier<Boolean> passwordCheck = () -> this.passWord.length() < 8;
        this.passWord.addTextChangedListener(createTextWatcher(passwordCheck,
                "Password requires at least 8 characters", this.passwordLayout));
        Supplier<Boolean> passwordConfirm = () -> !Objects.equals(this.passWord.getText().toString(),
                this.repassWord.getText().toString()) || this.repassWord.length() < 8;
        this.repassWord.addTextChangedListener(createTextWatcher(passwordConfirm,
                "Password does not match or is invalid", this.confirmPasswordLayout));
    }

    private boolean checkFields() {
        boolean allCorrect = true;

        if (TextUtils.isEmpty(emailAdd.getText().toString())) {
            this.emailAdd.setError("This field is required");
            allCorrect = false;
        }
        if (TextUtils.isEmpty(contactNo.getText().toString())) {
            this.contactNo.setError("This field is required");
            allCorrect = false;
        }
        if (this.passWord.length() < 8) {
            allCorrect = false;
        } else {
            if (!Objects.equals(this.passWord.getText().toString(),
                    this.repassWord.getText().toString())) {
                allCorrect = false;
            }
        }
        if (this.contactNo.length() != 8) {
            this.contactNo.setError("Invalid phone number");
            allCorrect = false;
        } else {
            try {
                Integer.parseInt(contactNo.getText().toString());
            } catch (NumberFormatException e) {
                this.contactNo.setError("Invalid Phone number");
            }
        }
        return allCorrect;
    }

    //bring forward the attributes to the subsequent pages
    //start the new Activity
    private void sendIntent () {
        Intent startSecondPage = new Intent(getApplicationContext(), SecBizRegActivity.class);
        Listings listing = new Listings();
        listing.setPhoneNumber(Integer.parseInt(this.contactNo.getText().toString()));
        startSecondPage.putExtra("emailAdd",this.emailAdd.getText().toString().trim());
        startSecondPage.putExtra("passWord",this.passWord.getText().toString());
        startSecondPage.putExtra("listing", listing);
        startActivity(startSecondPage);
    }

    private void checkIfValid() {
        if (checkFields()) {
            //check if email already in use
            mAuth.fetchSignInMethodsForEmail(emailAdd.getText().toString()).addOnCompleteListener(
                    new  OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean notInUse = task.getResult().getSignInMethods().isEmpty();
                            if (!notInUse) {
                                Toast.makeText(getApplicationContext(), "Email already in use",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                sendIntent();
                            }
                        }
                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show());
        }
    }
}