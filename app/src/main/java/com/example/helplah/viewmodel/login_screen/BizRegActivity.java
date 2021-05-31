package com.example.helplah.viewmodel.login_screen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helplah.R;
import com.google.firebase.auth.FirebaseAuth;

public class BizRegActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button backButton;
    private Button nextPage;
    private TextView errorMessage;
    private EditText emailAdd;
    private EditText passWord;
    private EditText repassWord;
    private EditText contactNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biz_reg);

        mAuth = FirebaseAuth.getInstance();
        this.errorMessage = (TextView) findViewById(R.id.errorMsg);
        this.emailAdd = (EditText) findViewById(R.id.registerEmail);
        this.passWord = (EditText) findViewById(R.id.registerPassword);
        this.repassWord = (EditText) findViewById(R.id.registerConfirmPassword);
        this.contactNo = (EditText) findViewById(R.id.registerContactNumber);
        this.nextPage = (Button) findViewById(R.id.nextPageButton);
        this.backButton = (Button) findViewById(R.id.registerBackButton);

        this.backButton.setOnClickListener(x -> this.finish());
        this.nextPage.setOnClickListener(x -> this.checkIfValid());
    }

    private boolean validatePassword() {
        if (TextUtils.isEmpty(this.passWord.getText().toString())) {
            return false;
        } else if (!this.passWord.getText().toString().equals(this.repassWord.getText().toString())) {
            return false;
        } else {
            return true;
        }
    }

    private int passwordLength() {
        return this.passWord.getText().toString().length();
    }

    //check if there are any empty fields
    private boolean noEmptyField() {
        if (TextUtils.isEmpty(emailAdd.getText().toString()) ||
                TextUtils.isEmpty(contactNo.getText().toString())) {
            return false;
        } else {
            return true;
        }
    }

    //check if the email is unique TODO
    private boolean checkEmailinUse() {
        return true;
    }


    //bring forward the attributes to the subsequent pages
    //start the new Activity
    private void sendIntent () {
        Intent startSecondPage = new Intent(getApplicationContext(), SecBizRegActivity.class);
        startSecondPage.putExtra("emailAdd",this.emailAdd.toString().trim());
        startSecondPage.putExtra("passWord",this.passWord.toString());
        startSecondPage.putExtra("contactNo",this.contactNo.toString());
        startActivity(startSecondPage);
    }

    private void checkIfValid() {
        if (!noEmptyField()) {
            errorMessage.setText("Please fill in all fields!");
        } else if (!this.validatePassword()) {
            errorMessage.setText("Password invalid!");
        } else if (passwordLength() < 8) {
            errorMessage.setText("Minimum password length is 8 characters!");
        } else if (!checkEmailinUse()) {
            errorMessage.setText("Email already in use!");
        }
        else {
            //proceed to next page
            //send Intent over
            sendIntent();
        }
    }
}