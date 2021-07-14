package com.example.helplah.viewmodel.business;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.helplah.APIs.JavaMailAPI;
import com.example.helplah.R;
import com.example.helplah.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Allows the user to write a complaint about the app. The complaint will be received by the app
 * administrator.
 */
public class WriteComplainFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DocumentReference userDoc;
    private ImageView backButton;
    private Button submitButton;
    private CheckBox userComplain;
    private CheckBox listingsComplain;
    private CheckBox requestsComplain;
    private CheckBox accountComplain;
    private CheckBox othersComplain;
    private EditText complainText;
    private String userEmail;
    private String id;
    private String adminEmail = "helplahofficial@gmail.com";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.write_complain_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        id = mAuth.getCurrentUser().getUid();

        backButton = v.findViewById(R.id.backCompButton);
        submitButton = v.findViewById(R.id.submitButton);
        userComplain = v.findViewById(R.id.checkBoxUserC);
        listingsComplain = v.findViewById(R.id.checkBoxListingC);
        requestsComplain = v.findViewById(R.id.checkBoxRequestC);
        accountComplain = v.findViewById(R.id.checkBoxAccountC);
        othersComplain = v.findViewById(R.id.checkBoxOthersC);
        complainText = v.findViewById(R.id.writeComplainBody);

        //initialise user's email
        userDoc = FirebaseFirestore.getInstance().collection(User.DATABASE_COLLECTION)
                .document(id);
        userDoc.addSnapshotListener(
                (value, error) -> userEmail = (String) value.get("email"));

        backButton.setOnClickListener(v1 -> getActivity().onBackPressed());

        submitButton.setOnClickListener(x -> {
            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Are you sure you want to submit this complaint")
                    .setPositiveButton("Submit", (dialog, which) -> submitComplaint())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        return v;
    }

    //to obtain the nature of complain(s)
    protected ArrayList<String> getSubject() {
        ArrayList<String> subject = new ArrayList<String>();
        if (userComplain.isChecked()) {
            subject.add("Related to User");
        }
        if (requestsComplain.isChecked()) {
            subject.add("Related to Requests");
        }
        if (listingsComplain.isChecked()) {
            subject.add("Related to listings");
        }
        if (accountComplain.isChecked()) {
            subject.add("Related to Account");
        }
        if (othersComplain.isChecked()) {
            subject.add("Others");
        }

        return subject;
    }

    protected void submitComplaint() {
        String complain ="Complain:" + complainText.getText().toString();
        String intro = "Nature of Complain:";
        ArrayList<String> types = getSubject();
        if (complainText.getText().toString().length() != 0 && types.size() != 0) {
            if (types.size() != 0) {
                for (int i = 0; i < types.size(); i++) {
                    intro = intro + types.get(i) + "/";
                }
            } else {
                //no boxes ticked; set nature of complain to others
                intro += "others";
            }
            String finalComplain = intro + "\n" + complain;
            sendEmail(finalComplain, userEmail);
            Toast.makeText(getActivity(), "Complain sent successfully", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {
            //Do nothing if nothing is written
            getActivity().onBackPressed();
        }
    }

    protected void sendEmail(String descp, String userEmail) {
        //subject set as the email of user.
        JavaMailAPI javaMailAPI = new JavaMailAPI(getContext(),adminEmail,userEmail,descp);

        //These strings are used for the confirmation email to users.
        String subjUser = "HelpLah: Acknowledgement of Complain.";
        String success = "Dear user, your complain has been received. We will get back to you as soon as possible, thank you.";
        JavaMailAPI confirmationEmail = new JavaMailAPI(getContext(),userEmail,subjUser,success);
        javaMailAPI.execute();
        confirmationEmail.execute();
    }
}