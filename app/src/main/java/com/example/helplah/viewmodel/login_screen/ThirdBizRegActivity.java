package com.example.helplah.viewmodel.login_screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.CheckboxAdapter;
import com.example.helplah.models.Listings;
import com.example.helplah.models.Services;
import com.example.helplah.models.User;
import com.example.helplah.viewmodel.consumer.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ThirdBizRegActivity extends AppCompatActivity {

    private static final String TAG = "Business Registration screen";

    private FirebaseAuth mAuth;
    private Listings listing;

    private Button backButton;
    private Button regButton;
    private String emailAdd;
    private String passWord;
    private String address;
    private String[] languages;
    private String[] services;
    private RecyclerView langBox;
    private RecyclerView serviceBox;
    private CheckboxAdapter languageAdapter;
    private CheckboxAdapter serviceAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biz_reg3);


        this.mAuth = FirebaseAuth.getInstance();
        this.emailAdd = getIntent().getExtras().getString("emailAdd");
        this.passWord = getIntent().getExtras().getString("passWord");
        this.listing = getIntent().getExtras().getParcelable("listing");
        this.address = getIntent().getExtras().getString("bizAdd");
        this.regButton = findViewById(R.id.regButton);
        this.backButton = findViewById(R.id.registerBackButton);

        this.languages = Listings.ALL_LANGUAGES.toArray(new String[4]);
        this.services = Services.ALLSERVICES.toArray(new String[Services.ALLSERVICES.size()]);

        this.langBox = findViewById(R.id.langBoxes);
        this.serviceBox = findViewById(R.id.servBoxes);

        //handling the grid views
        this.languageAdapter = new CheckboxAdapter(this, languages);
        this.serviceAdapter = new CheckboxAdapter(this,services);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false);
        langBox.setAdapter(languageAdapter);
        serviceBox.setAdapter(serviceAdapter);
        langBox.setLayoutManager(gridLayoutManager1);
        serviceBox.setLayoutManager(gridLayoutManager2);

        this.backButton.setOnClickListener(x -> this.finish());
        this.regButton.setOnClickListener(x -> this.createBiz());

    }


    private void createBiz() {
        Log.d(TAG, "createBiz: " + emailAdd);
        Log.d(TAG, "createBiz: " + passWord);
        this.mAuth.createUserWithEmailAndPassword(emailAdd,passWord)
                .addOnCompleteListener(ThirdBizRegActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG,"onComplete: Completed registration");
                        if (task.isSuccessful()) {
                            //Successfully created user
                            FirebaseUser bizUser = mAuth.getCurrentUser();
                            if (bizUser == null) {
                                Toast.makeText(ThirdBizRegActivity.this,"Registration failed",Toast.LENGTH_LONG).show();
                            } else {
                                addBizUserToFirestore(bizUser.getUid());
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            //Failed to create user
                            Toast.makeText(ThirdBizRegActivity.this,
                                    "Registration failed",Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private void addBizUserToFirestore(String id) {

        ArrayList<String> languageList = (ArrayList<String>) this.languageAdapter.listofSelected();
        ArrayList<String> serviceList = (ArrayList<String>) this.serviceAdapter.listofSelected();
        Services availServices = new Services(serviceList);
        this.listing.setServicesList(availServices);
        this.listing.setCompany(true);
        String language = ""; //TODO

        CollectionReference businessCollection = FirebaseFirestore.getInstance().collection(Listings.DATABASE_COLLECTION);
        CollectionReference userCollection = FirebaseFirestore.getInstance().collection(User.DATABASE_COLLECTION);

        //Updating the collections for user and businesses with the same user id
        User user = new User(this.listing.getName(), this.emailAdd, this.listing.getPhoneNumber(),true);
        user.setAddress(this.address);
        user.setUserId(id);
        userCollection.document(id).set(user);
        businessCollection.document(id).set(this.listing);
    }
}
