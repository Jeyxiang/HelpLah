package com.example.helplah;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helplah.models.RandomData;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference businessRef = db.collection("Businesses");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = findViewById(R.id.testButton2);
        Button addButton = findViewById(R.id.addButton);
        Context c =getApplicationContext();
        b.setOnClickListener(v -> {
            Intent i = new Intent(this, BusinessListings.class);
            startActivity(i);
        });
        addButton.setOnClickListener(v -> {
            RandomData.fillDatabase(10);
        });
    }

}