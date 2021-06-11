package com.example.helplah.viewmodel.business;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.helplah.R;
import com.example.helplah.models.Listings;
import com.example.helplah.models.Services;
import com.example.helplah.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class EditListingFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "Edit Listings";

    private FirebaseAuth mAuth;
    private EditText editName;
    private EditText editWeb;
    private EditText editDescription;
    private EditText editMinPrice;
    private EditText editPriceNote;
    private EditText editNumber;
    private Spinner editAvailability;
    private CheckBox checkboxPlumbing;
    private CheckBox checkboxElectrician;
    private CheckBox checkboxAircon;
    private CheckBox checkboxClean;
    private CheckBox checkboxMovers;
    private CheckBox checkboxLockSmith;
    private CheckBox checkboxPaint;
    private CheckBox checkBoxPest;
    private CheckBox checkBoxCarWash;
    private CheckBox checkBoxLaundry;
    private Listings updatedListing;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.edit_listings_fragment, container, false);

        this.mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();

        ImageView backButton = v.findViewById(R.id.backButton);
        Button updateButton = v.findViewById(R.id.updateButton);
        this.editName = v.findViewById(R.id.editNameListing);
        this.editWeb = v.findViewById(R.id.editWebListing);
        this.editDescription = v.findViewById(R.id.editDescListing);
        this.editNumber = v.findViewById(R.id.editContactNumberListing);
        this.editMinPrice = v.findViewById(R.id.editMinPriceListing);
        this.editPriceNote = v.findViewById(R.id.editPriceNoteListing);
        this.checkboxPlumbing = v.findViewById(R.id.checkBoxPlumbing);
        this.checkboxElectrician = v.findViewById(R.id.checkBoxElectrician);
        this.checkboxAircon = v.findViewById(R.id.checkBoxAirCon);
        this.checkboxClean = v.findViewById(R.id.checkBoxCleaning);
        this.checkboxMovers = v.findViewById(R.id.checkBoxMovers);
        this.checkboxLockSmith = v.findViewById(R.id.checkBoxLockSmith);
        this.checkboxPaint = v.findViewById(R.id.checkBoxPaint);
        this.checkBoxPest = v.findViewById(R.id.checkBoxPest);
        this.checkBoxCarWash = v.findViewById(R.id.checkBoxCarWash);
        this.checkBoxLaundry = v.findViewById(R.id.checkBoxLaundry);

        this.editAvailability = v.findViewById(R.id.availability);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.business_availability,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.editAvailability.setAdapter(adapter);

        DocumentReference userDoc = FirebaseFirestore.getInstance().collection(Listings.DATABASE_COLLECTION)
                .document(id);

        //Obtain the necessary data from the database
        userDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent( DocumentSnapshot value, FirebaseFirestoreException error) {

                updatedListing = value.toObject(Listings.class);
                assert updatedListing != null;
                editName.setText(updatedListing.getName());
                editWeb.setText(updatedListing.getWebsite());
                editDescription.setText(updatedListing.getDescription());
                editNumber.setText(updatedListing.getPhoneNumber() + "");
                editMinPrice.setText(updatedListing.getMinPrice() + "");
                editPriceNote.setText(updatedListing.getPricingNote());
                editAvailability.setSelection(updatedListing.getAvailability() - 1);


                //Check the language boxes TODO

                ArrayList<String> arrServ = updatedListing.getServicesList().getServicesProvided();

                for (String s : arrServ) {
                    if (s.equals(Services.PLUMBER)) {
                        checkboxPlumbing.setChecked(true);
                    } else if (s.equals(Services.ELECTRICIAN)) {
                        checkboxElectrician.setChecked(true);
                    } else if (s.equals(Services.AIRCONDITIONING)) {
                        checkboxAircon.setChecked(true);
                    } else if (s.equals(Services.CLEANER)) {
                        checkboxClean.setChecked(true);
                    } else if (s.equals(Services.MOVERS)) {
                        checkboxMovers.setChecked(true);
                    } else if (s.equals(Services.LOCKSMITH)) {
                        checkboxLockSmith.setChecked(true);
                    } else if (s.equals(Services.PAINTERS)) {
                        checkboxPaint.setChecked(true);
                    } else if (s.equals(Services.PEST_CONTROL)) {
                        checkBoxPest.setChecked(true);
                    } else if (s.equals(Services.CAR_WASH)) {
                        checkBoxCarWash.setChecked(true);
                    } else if (s.equals(Services.LAUNDRY)) {
                        checkBoxLaundry.setChecked(true);
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        updateButton.setOnClickListener(x -> saveChanges());

        return v;
    }


    public void saveChanges() {
        //check if the necessary fields are filled, and checkboxes for Services are checked
        if (checkFields() && checkServBoxes().getServicesProvided().size() != 0) {
            String newName = this.editName.getText().toString().trim();
            String newWeb = this.editWeb.getText().toString().trim();
            String newDescription = this.editDescription.getText().toString().trim();
            String newPrice = this.editMinPrice.getText().toString();
            String newNote = this.editPriceNote.getText().toString().trim();
            int newNumber = Integer.parseInt(this.editNumber.getText().toString().trim());
            int newAvailability = this.editAvailability.getSelectedItemPosition() + 1;

            //save Data
            this.updatedListing.setName(newName);
            this.updatedListing.setPhoneNumber(newNumber);
            this.updatedListing.setWebsite(newWeb);
            this.updatedListing.setDescription(newDescription);
            this.updatedListing.setMinPrice(Double.parseDouble(newPrice));
            this.updatedListing.setPricingNote(newNote);
            this.updatedListing.setAvailability(newAvailability);

            // Update the user database
            String id = this.mAuth.getCurrentUser().getUid();
            User.updateUsername(id, newName);
            User.updatePhoneNumber(id, newNumber);

            //Check the checkboxes
            Services servLst = checkServBoxes();
            this.updatedListing.setServicesList(servLst);

            //update language TODO
            ArrayList<String> langlst = checkLangBoxes();
            this.updatedListing.setLanguage("");

            //update server with updated list;
            CollectionReference businessCollection = FirebaseFirestore.getInstance().collection(Listings.DATABASE_COLLECTION);
            businessCollection.document(id).set(updatedListing);
            Toast.makeText(getActivity(),"Update Successful",Toast.LENGTH_LONG).show();
            getActivity().onBackPressed();
            Log.d(TAG,"Update Successful");

        } else {
            //update cannot go through
            Log.d(TAG,"Update failed to go through");
            Toast.makeText(getActivity(),"Update failed",Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkFields() {
        boolean allCorrect = true;

        if (TextUtils.isEmpty(editName.getText().toString())) {
            this.editName.setError("This field is required");
            allCorrect = false;
        }
        if (TextUtils.isEmpty(editDescription.getText().toString())) {
            this.editDescription.setError("This field is required");
            allCorrect = false;
        }
        if (TextUtils.isEmpty(editMinPrice.getText().toString())) {
            this.editMinPrice.setError("This field is required");
            allCorrect = false;
        }
        if (TextUtils.isEmpty(editPriceNote.getText().toString())) {
            this.editPriceNote.setError("This field is required");
            allCorrect = false;
        }
        return allCorrect;
    }


    public Services checkServBoxes() {
        //check services, at least one box ticked
        Services serv = new Services();
        if (checkboxElectrician.isChecked()) {
            serv.addService(Services.ELECTRICIAN);
        }
        if (checkboxPlumbing.isChecked()) {
            serv.addService(Services.PLUMBER);
        }
        if (checkboxAircon.isChecked()) {
            serv.addService(Services.AIRCONDITIONING);
        }
        if (checkboxMovers.isChecked()) {
            serv.addService(Services.MOVERS);
        }
        if (checkboxPaint.isChecked()) {
            serv.addService(Services.PAINTERS);
        }
        if (checkboxLockSmith.isChecked()) {
            serv.addService(Services.LOCKSMITH);
        }
        if (checkBoxPest.isChecked()) {
            serv.addService(Services.PEST_CONTROL);
        }
        if (checkboxClean.isChecked()) {
            serv.addService(Services.CLEANER);
        }
        if (checkBoxCarWash.isChecked()) {
            serv.addService(Services.CAR_WASH);
        }
        if (checkBoxLaundry.isChecked()) {
            serv.addService(Services.LAUNDRY);
        }
        if (serv.getServicesProvided().size() == 0) {
            Toast.makeText(getActivity(),"Please select at least one service",Toast.LENGTH_LONG).show();
        }
        return serv;
    }
    //TODO
    public ArrayList<String> checkLangBoxes() {
        ArrayList<String> lang = new ArrayList<String>();
        return lang;
    }

    //For the spinner Adapter
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }
}