package com.example.helplah.viewmodel.login_screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helplah.R;
import com.google.firebase.auth.FirebaseAuth;

public class SecBizRegActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button backButton;
    private Button nextPage;
    private String emailAdd;
    private String passWord;
    private String contactNo;
    private EditText bizName;
    private EditText bizAddress;
    private EditText webAddress;
    private EditText descrp;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biz_reg2);


        mAuth = FirebaseAuth.getInstance();
        this.emailAdd = getIntent().getExtras().getString("emailAdd");
        this.passWord =  getIntent().getExtras().getString("passWord");
        this.contactNo = getIntent().getExtras().getString("contactNo");
        this.bizName = (EditText) findViewById(R.id.registerBizName);
        this.bizAddress = (EditText) findViewById(R.id.registerAddress);
        this.webAddress = (EditText) findViewById(R.id.registerWebsite);
        this.descrp = (EditText) findViewById(R.id.registerDescrp);
        this.errorMessage = (TextView) findViewById(R.id.errorMsg);
        this.backButton = (Button) findViewById(R.id.registerBackButton);
        this.nextPage = (Button) findViewById(R.id.nextPageButton);

        this.backButton.setOnClickListener(x -> this.finish());
        this.nextPage.setOnClickListener(x -> this.checkIfValid());

    }

    //other than description and website, the other fields cannot be empty
    private boolean noEmptyField() {
        if (TextUtils.isEmpty(bizName.getText().toString()) ||
                TextUtils.isEmpty(bizAddress.getText().toString())) {
            return false;
        } else {
            return true;
        }
    }
    private void sendIntent () {
//        Intent startThirdPage = new Intent(getApplicationContext(), thirdbizRegActivity.class);
//        startThirdPage.putExtra("emailAdd",this.emailAdd);
//        startThirdPage.putExtra("passWord",this.passWord);
//        startThirdPage.putExtra("contactNo",this.contactNo);
//        startThirdPage.putExtra("bizName",this.bizName.toString().trim());
//        startThirdPage.putExtra("bizAdd",this.bizAddress.toString().trim());
//        startThirdPage.putExtra("webAdd",this.webAddress.toString().trim());
//        startThirdPage.putExtra("description",this.descrp.toString());
//        startActivity(startThirdPage);
    }

    private boolean workableLink() {
        if (TextUtils.isEmpty(webAddress.getText().toString())) {
            //no check required
            return true;
        } else {
            Uri webLink = Uri.parse(webAddress.getText().toString());
            Intent gotoAddress = new Intent(Intent.ACTION_VIEW,webLink);
            if (gotoAddress.resolveActivity(getPackageManager()) != null) {
                return true;
            } else {
                return false;
            }

        }
    }

    private void checkIfValid() {
        if (!noEmptyField()) {
            errorMessage.setText("Please fill in all fields!");
        } else if (!this.workableLink()) {
            errorMessage.setText("Please use a workable website link!");
        } else {
            //proceed to next page
            //send Intent over
            sendIntent();
        }
    }
}