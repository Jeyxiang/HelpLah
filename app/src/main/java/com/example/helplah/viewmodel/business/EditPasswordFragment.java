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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.helplah.R;
import com.example.helplah.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

/**
 * Allows a user to change his password.
 */
public class EditPasswordFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DocumentReference userDoc;
    private String id;
    private String TAG = "Password change Event";
    private String userEmail;
    private EditText oldPassword;
    private EditText newPassword1;
    private EditText newPassword2;
    private Button updatePWButton;
    private ImageView backButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.edit_password_fragment, container, false);
        updatePWButton = v.findViewById(R.id.updatePWButton);
        backButton = v.findViewById(R.id.backPWButton);
        oldPassword = v.findViewById(R.id.oldPassword);
        newPassword1 = v.findViewById(R.id.editPassword1);
        newPassword2 = v.findViewById(R.id.editPassword2);

        mAuth = FirebaseAuth.getInstance();
        id = mAuth.getCurrentUser().getUid();

        userDoc = FirebaseFirestore.getInstance().collection(User.DATABASE_COLLECTION)
                .document(id);
        //initialise the Original user's email.
        userDoc.addSnapshotListener(
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        userEmail = (String) value.get("email");
                    }
                });
        backButton.setOnClickListener(x -> getActivity().onBackPressed());
        updatePWButton.setOnClickListener(x -> updatedPassword());
        return v;
    }

    public void updatedPassword() {
        String oldPW = oldPassword.getText().toString();
        String newPW = newPassword1.getText().toString();
        if (checkFields()) {
            //check if original password corresponds with the one in database first
            mAuth.signInWithEmailAndPassword(userEmail, oldPW)
                    .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Old Password Correct");
                                //update password
                                mAuth.getCurrentUser().updatePassword(newPW).addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Account Updated Successfully");
                                                    Toast.makeText(getContext(), "Password successfully updated!", Toast.LENGTH_LONG).show();
                                                    getActivity().onBackPressed();
                                                } else {
                                                    Log.d(TAG, "Please try again");
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(getContext(), "Wrong original Password!Please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private boolean checkFields() {
        boolean allCorrect = true;

        if (TextUtils.isEmpty(oldPassword.getText().toString())) {
            this.oldPassword.setError("This field is required");
            allCorrect = false;
        }
        if (TextUtils.isEmpty(newPassword1.getText().toString())) {
            this.newPassword1.setError("This field is required");
            allCorrect = false;
        }
        if (TextUtils.isEmpty(newPassword2.getText().toString())) {
            this.newPassword2.setError("This field is required");
            allCorrect = false;
        }
        if (!newPassword1.getText().toString().equals(newPassword2.getText().toString())) {
            Toast.makeText(getContext(), "New passwords does not match", Toast.LENGTH_LONG).show();
            allCorrect = false;
        }
        if (newPassword1.getText().toString().length() < 8) {
            this.newPassword1.setError("Password requirement is at least 8 characters");
            allCorrect = false;
        }
        if (newPassword2.getText().toString().length() < 8) {
            this.newPassword2.setError("Password requirement is at least 8 characters");
            allCorrect = false;
        }
        if (newPassword1.getText().toString().equals(oldPassword.getText().toString())) {
            Toast.makeText(getContext(), "No difference with old password", Toast.LENGTH_LONG).show();
            allCorrect = false;
        }
        return allCorrect;
    }
}