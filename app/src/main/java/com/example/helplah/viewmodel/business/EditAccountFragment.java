package com.example.helplah.viewmodel.business;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.helplah.R;
import com.example.helplah.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

/**
 * Allows the user to edit his account information and syncs it into the firestore database.
 */
public class EditAccountFragment extends Fragment {

    private static final String TAG = "Edit Listings";
    private FirebaseAuth mAuth;
    private String oldEmail;
    private DocumentReference userDoc;
    private EditText editEmail;
    private EditText editLocation;
    private EditText editPostal;
    private Button updateAcctButton;
    private ImageView backAcctButton;
    private User newUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.business_edit_account_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();


        editEmail = v.findViewById(R.id.editEmailAcct);
        editLocation = v.findViewById(R.id.editLocationAcct);
        editPostal = v.findViewById(R.id.editPostalAcct);

        updateAcctButton = v.findViewById(R.id.updateAcctButton);
        backAcctButton = v.findViewById(R.id.backAcctButton);

        newUser = new User();

        userDoc = FirebaseFirestore.getInstance().collection(User.DATABASE_COLLECTION)
                .document(id);


        userDoc.addSnapshotListener((value, error) -> {
            //initialise current values
            User user = value.toObject(User.class);
            oldEmail = user.getEmail();
            editEmail.setText(oldEmail);
            editLocation.setText(user.getAddress());
            if (user.getPostalCode() != 0) {
                editPostal.setText(user.getPostalCode() + "");
            }

            newUser.setUsername(user.getUsername());
            newUser.setBusiness(true);
        });

        //initialise buttons

        backAcctButton.setOnClickListener(x -> getActivity().onBackPressed());
        updateAcctButton.setOnClickListener(x -> updateChanges());


        return v;
    }

    //Check email format
    private boolean isEmailValid(CharSequence email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        } else {
            Toast.makeText(getActivity(), "Please enter a valid Email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void updateChanges() {
        if (checkFields()) {
            //check if email field has changed, then run the test on the new email
            String newEmail = editEmail.getText().toString().trim();
            if (emailChanged()) {
                mAuth.fetchSignInMethodsForEmail(newEmail).addOnCompleteListener(
                        task -> {
                            boolean notInUse = task.getResult().getSignInMethods().isEmpty();
                            if (!notInUse) {
                                Toast.makeText(getActivity(), "Email already in use",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                updateAccount();
                            }
                        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Invalid email", Toast.LENGTH_SHORT).show());
            } else {
                updateAccount();
            }
        } else {
            //update cannot go through
            Log.d(TAG,"Update failed to go through");
            Toast.makeText(getActivity(),"Update failed",Toast.LENGTH_LONG).show();
        }

    }

    public boolean emailChanged() {
        String newEmail = editEmail.getText().toString().trim();
        return (!newEmail.equals(oldEmail));
    }

    public void updateAccount() {
        String newEmail = editEmail.getText().toString().trim();
        String newLoc = editLocation.getText().toString().trim();
        String newPostal= editPostal.getText().toString().trim();

        newUser.setEmail(newEmail);
        newUser.setAddress(newLoc);
        newUser.setPostalCode(Integer.parseInt(newPostal));
        //Update Email
        if (emailChanged()) {
            mAuth.getCurrentUser().updateEmail(newEmail).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Account Updated Successfully");
                        }
                    });
        }
        //update server with updated User;
        CollectionReference userCollection = FirebaseFirestore.getInstance().collection(User.DATABASE_COLLECTION);
        String id = mAuth.getCurrentUser().getUid();
        userCollection.document(id).set(newUser).addOnSuccessListener(unused -> {
            Toast.makeText(getActivity(),"Account Updated Successfully",Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
            Log.d(TAG,"Account Updated Successfully");
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getActivity(),"Account failed to update: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                Log.d(TAG,"Account Updated Successfully");
            }
        });
    }

    public boolean checkFields() {
        boolean allCorrect = true;
        if (TextUtils.isEmpty(editEmail.getText().toString())) {
            this.editEmail.setError("This field is required");
            allCorrect = false;
        } else {
            //check format of the email
            if (isEmailValid(editEmail.getText().toString())) {
                allCorrect = true;
            } else {
                this.editEmail.setError("Invalid Email Format");
                allCorrect = false;
            }
        }
        if (TextUtils.isEmpty(editLocation.getText().toString())) {
            this.editLocation.setError("This field is required");
            allCorrect = false;
        }
        if (TextUtils.isEmpty(editPostal.getText().toString())) {
            this.editPostal.setError("This field is required");
            allCorrect = false;
        }
        return allCorrect;
    }
}