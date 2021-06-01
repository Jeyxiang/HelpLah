package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.helplah.R;
import com.example.helplah.models.Listings;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListingDescription} factory method to
 * create an instance of this fragment.
 */
public class ListingDescription extends Fragment {

    private static final String TAG = "Listing description";

    private CollectionReference businessCollection;
    private Listings listing;

    public ListingDescription() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.businessCollection = FirebaseFirestore.getInstance().collection(Listings.DATABASE_COLLECTION);
        Log.d(TAG, "onCreate: called");
        String id = this.getArguments().getString("id");

        if (id != null) {
            this.businessCollection.whereEqualTo(FieldPath.documentId(), id).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                listing = snapshot.toObject(Listings.class);
                                Log.d(TAG, "onSuccess: " + listing.getName());
                            }
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "An unexpected error occurred", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listing_description, container, false);
    }
}