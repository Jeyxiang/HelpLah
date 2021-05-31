package com.example.helplah.viewmodel.login_screen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helplah.R;
import com.example.helplah.models.User;
import com.example.helplah.viewmodel.consumer.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.function.Supplier;

public class RegisterScreen extends AppCompatActivity {

    private static final String TAG = "Registration screen";

    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;

    private Button regAsUser;
    private TextView regAsBiz;
    private EditText mEmail;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mPhoneNumber;

    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        this.passwordLayout = findViewById(R.id.passwordLayout);
        this.confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        this.regAsUser = findViewById(R.id.registerButton);
        this.regAsBiz = findViewById(R.id.bizRegisterButton);
        Button backButton = findViewById(R.id.registerBackButton);
        this.mEmail = findViewById(R.id.registerEmail);
        this.mUsername = findViewById(R.id.registerUserName);
        this.mPassword = findViewById(R.id.registerPassword);
        this.mConfirmPassword = findViewById(R.id.registerConfirmPassword);
        this.mPhoneNumber = findViewById(R.id.registerPhoneNumber);
        backButton.setOnClickListener(x -> this.finish());
        this.regAsUser.setOnClickListener(x -> userRegister());

        this.mAuth = FirebaseAuth.getInstance();

        setTextWatcher();

        regAsBiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BizRegActivity.class));
            }
        });
    }

    private void userRegister() {
        String email = this.mEmail.getText().toString().trim();
        String password = this.mPassword.getText().toString().trim();
        if (checkFields()) {
            createUser(email, password);
        }
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
        Supplier<Boolean> passwordCheck = () -> this.mPassword.length() < 8;
        this.mPassword.addTextChangedListener(createTextWatcher(passwordCheck,
                "Password requires at least 8 characters", this.passwordLayout));
        Supplier<Boolean> passwordConfirm = () -> !Objects.equals(this.mPassword.getText().toString(),
                this.mConfirmPassword.getText().toString()) || this.mConfirmPassword.length() < 8;
        this.mConfirmPassword.addTextChangedListener(createTextWatcher(passwordConfirm,
                "Password does not match or is invalid", this.confirmPasswordLayout));
    }

    private boolean checkFields() {
        boolean allCorrect = true;
        if (this.mEmail.length() == 0) {
            this.mEmail.setError("This field is required");
            allCorrect = false;
        }
        if (this.mUsername.length() == 0) {
            this.mUsername.setError("This field is required");
            allCorrect = false;
        }
        if (this.mPhoneNumber.length() != 8) {
            this.mPhoneNumber.setError("Invalid phone number");
            allCorrect = false;
        } else {
            try {
                Integer.parseInt(this.mPhoneNumber.getText().toString());
            } catch (NumberFormatException e) {
                this.mPhoneNumber.setError("Invalid phone number");
                allCorrect = false;
            }
        }
        if (this.mPassword.length() < 8) {
            allCorrect = false;
        } else {
            if (!Objects.equals(this.mPassword.getText().toString(),
                    this.mConfirmPassword.getText().toString())) {
                allCorrect = false;
            }
        }
        return allCorrect;
    }

    private void createUser(String email, String password) {
        this.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterScreen.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "onComplete: Completed registration");
                        if (task.isSuccessful()) {
                            // Successfully created user
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user == null) {
                                Toast.makeText(RegisterScreen.this,
                                        "Registration Failed", Toast.LENGTH_LONG);
                            } else {
                                addUserToFirestore(user.getUid());
                                Intent intent = new Intent(getApplicationContext(),
                                        MainActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            // Failed to create user
                            Toast.makeText(RegisterScreen.this,
                                    "Registration Failed", Toast.LENGTH_LONG);
                        }
                    }
                });
    }

    private void addUserToFirestore(String id) {
        String phoneNumber = this.mPhoneNumber.getText().toString();
        User user = new User(Integer.parseInt(phoneNumber), false);

        CollectionReference mCollection = FirebaseFirestore.getInstance()
                                                .collection(User.DATABASE_COLLECTION);

        mCollection.document(id).set(user);
    }
}