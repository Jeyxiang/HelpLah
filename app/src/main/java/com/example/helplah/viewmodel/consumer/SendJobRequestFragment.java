package com.example.helplah.viewmodel.consumer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.helplah.R;
import com.example.helplah.models.JobRequests;
import com.example.helplah.models.Listings;
import com.example.helplah.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SendJobRequestFragment} factory method to
 * create an instance of this fragment.
 */
public class SendJobRequestFragment extends Fragment {

    private static final String TAG = "Send job request fragment";

    private View rootView;
    private Listings listing;
    private MaterialDatePicker datePicker;

    private EditText jobDescription;
    private EditText jobDateNote;
    private TextView jobDate;
    private EditText jobAddress;
    private EditText jobNumber;
    private TextView businessName;
    private TextView businessScore;
    private TextView businessPopularity;

    private String businessId;
    private String userId;
    private String username;
    private String address;
    private int phoneNumber;
    private String category;
    private Date date;
    private boolean queryDone = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: called");
        this.listing = this.getArguments().getParcelable("listing");
        this.businessId = this.getArguments().getString("id");
        this.category = this.getArguments().getString("category");

        if (this.listing == null || this.businessId == null || this.category == null) {
            Toast.makeText(getActivity(), "An unexpected error occurred", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView =  inflater.inflate(R.layout.fragment_send_job_request, container, false);

        this.jobDescription = this.rootView.findViewById(R.id.jobDescription);
        this.jobDateNote = this.rootView.findViewById(R.id.jobDateNote);
        this.jobDate = this.rootView.findViewById(R.id.jobDate);
        this.businessName = this.rootView.findViewById(R.id.businessName);
        this.jobAddress = this.rootView.findViewById(R.id.jobAddress);
        this.jobNumber = this.rootView.findViewById(R.id.jobPhoneNumber);
        this.businessScore = this.rootView.findViewById(R.id.businessScore);
        this.businessPopularity = this.rootView.findViewById(R.id.businessPopularity);

        getUserInformation();
        bind();

        return this.rootView;
    }

    @SuppressLint("DefaultLocale")
    private void bind() {
        ImageView backButton = this.rootView.findViewById(R.id.requestBackButton);
        ExtendedFloatingActionButton sendButton = this.rootView.findViewById(R.id.jobSendButton);

        CalendarConstraints.DateValidator validator = DateValidatorPointForward.from(System.currentTimeMillis() - 80000000);
        CalendarConstraints.Builder constraint = new CalendarConstraints.Builder();
        constraint.setValidator(validator);

        this.datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Select date for job")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraint.build())
                .build();

        backButton.setOnClickListener(x -> requireActivity().onBackPressed());
        sendButton.setOnClickListener(x -> sendRequest());
        this.jobDate.setOnClickListener(x -> setDate());
        this.businessName.setText(this.listing.getName());
        this.businessScore.setText(String.format("%.1f", listing.getReviewScore()));
        this.businessPopularity.setText("(" + this.listing.getNumberOfReviews() + " reviews)");
    }

    @SuppressLint("SetTextI18n")
    private void setDate() {

        this.datePicker.show(getParentFragmentManager(), TAG);

        this.datePicker.addOnPositiveButtonClickListener(selection -> {
                jobDate.setText("Selected Date: " + datePicker.getHeaderText());
                date = new Date(Long.parseLong(selection.toString()));
        });
    }


    private boolean checkFields() {
        boolean allCorrect = true;
        if (this.jobDescription.length() == 0) {
            this.jobDescription.setError("This field cannot be empty");
            allCorrect = false;
        }
        if (this.jobDateNote.length() == 0) {
            this.jobDateNote.setError("This field cannot be empty");
            allCorrect = false;
        }
        if (this.jobNumber.length() != 8) {
            this.jobNumber.setError("Invalid phone number");
            allCorrect = false;
        }
        if (this.jobAddress.length() == 0) {
            this.jobAddress.setError("This field cannot be empty");
            allCorrect = false;
        }
        if (this.jobDate.getText().toString().equals("Choose a date")) {
            this.jobDate.setError("This field cannot be empty");
            allCorrect = false;
        }
        return allCorrect;
    }

    private void sendRequest() {
        if (checkFields() && queryDone) {
            // send request
            JobRequests requests = new JobRequests(this.userId, this.businessId, this.category);
            requests.setCustomerName(this.username);
            requests.setBusinessName(this.listing.getName());
            requests.setAddress(this.jobAddress.getText().toString());
            requests.setPhoneNumber(Integer.parseInt(this.jobNumber.getText().toString().trim()));
            requests.setJobDescription(this.jobDescription.getText().toString());
            requests.setDateOfJob(this.date);
            requests.setTimingNote(this.jobDateNote.getText().toString());

            DateFormat formatter = new SimpleDateFormat("E, dd MMM");

            new MaterialAlertDialogBuilder(getActivity())
                    .setTitle(getString(R.string.send_job_request_confirmation))
                    .setMessage("Job request scheduled for " + formatter.format(this.date))
                    .setPositiveButton("Send request", (dialog, which) -> addToDataBase(requests))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    private void addToDataBase(JobRequests requests) {
        CollectionReference dbRequests = FirebaseFirestore.getInstance().collection(JobRequests.DATABASE_COLLECTION);
        dbRequests.add(requests)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Request sent", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();})
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(),
                                "Request failed to send", Toast.LENGTH_SHORT).show());
    }

    private void getUserInformation() {

        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CollectionReference db = FirebaseFirestore.getInstance().collection(User.DATABASE_COLLECTION);

        Task a = db.whereEqualTo(FieldPath.documentId(), this.userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                User user = snapshot.toObject(User.class);
                username = user.getUsername();
                phoneNumber = user.getPhoneNumber();
                jobNumber.setText(String.valueOf(phoneNumber));
                address = user.getAddress();
                jobAddress.setText(address);
                queryDone = true;
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getActivity(), "An unexpected error occurred", Toast.LENGTH_SHORT).show());
    }
}