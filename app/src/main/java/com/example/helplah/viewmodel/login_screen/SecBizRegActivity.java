package com.example.helplah.viewmodel.login_screen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helplah.R;
import com.example.helplah.models.Listings;

public class SecBizRegActivity extends AppCompatActivity {

    private Listings listing;

    private Button backButton;
    private Button nextPage;
    private String emailAdd;
    private String passWord;
    private EditText bizAddress;
    private EditText bizName;
    private EditText webAddress;
    private EditText descrp;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biz_reg2);

        this.emailAdd = getIntent().getExtras().getString("emailAdd");
        this.passWord =  getIntent().getExtras().getString("passWord");
        this.listing = getIntent().getExtras().getParcelable("listing");
        this.bizName = findViewById(R.id.registerBizName);
        this.bizAddress = findViewById(R.id.registerAddress);
        this.webAddress =  findViewById(R.id.registerWebsite);
        this.descrp = findViewById(R.id.registerDescrp);
        this.errorMessage = findViewById(R.id.errorMsg);
        this.backButton = findViewById(R.id.registerBackButton);
        this.nextPage = findViewById(R.id.nextPageButton);

        this.backButton.setOnClickListener(x -> this.finish());
        this.nextPage.setOnClickListener(x -> this.checkIfValid());

    }

    //other than description and website, the other fields cannot be empty
    private boolean noEmptyField() {
        return !TextUtils.isEmpty(bizName.getText().toString()) &&
                !TextUtils.isEmpty(bizAddress.getText().toString()) &&
                !TextUtils.isEmpty(descrp.getText().toString());
    }
    private void sendIntent () {
        Intent startThirdPage = new Intent(getApplicationContext(), ThirdBizRegActivity.class);
        startThirdPage.putExtra("emailAdd", this.emailAdd);
        startThirdPage.putExtra("passWord", this.passWord);
        startThirdPage.putExtra("bizAdd", this.bizAddress.getText().toString());
        this.listing.setName(this.bizName.getText().toString().trim());
        this.listing.setWebsite(this.webAddress.getText().toString().trim());
        this.listing.setDescription(this.descrp.getText().toString());
        startThirdPage.putExtra("listing", this.listing);
        startActivity(startThirdPage);
    }

    private boolean workableLink() {
        return true;
//        if (TextUtils.isEmpty(webAddress.getText().toString())) {
//            //no check required
//            return true;
//        } else {
//            Uri webLink = Uri.parse(webAddress.getText().toString());
//            Intent gotoAddress = new Intent(Intent.ACTION_VIEW,webLink);
//            if (gotoAddress.resolveActivity(getPackageManager()) != null) {
//                return true;
//            } else {
//                return false;
//            }
//
//        }
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