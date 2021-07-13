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
import com.example.helplah.models.NotificationHandler;
import com.example.helplah.models.ProfilePictureHandler;
import com.example.helplah.models.User;
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
    private EditText jobPostalCode;
    private TextView businessName;
    private TextView businessScore;
    private TextView businessPopularity;
    private ExtendedFloatingActionButton sendButton;
    private JobRequests previousRequest;
    private String previousRequestId;

    private String businessId;
    private String userId;
    private String username;
    private String address;
    private int postalCode;
    private int phoneNumber;
    private String category;
    private Date date;
    private boolean queryDone = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: called");

        try {
            this.listing = this.getArguments().getParcelable("listing");
            this.businessId = this.getArguments().getString("id");
            this.category = this.getArguments().getString("category");
        } catch (Exception e) {
            Toast.makeText(getActivity(), "An unexpected error occurred", Toast.LENGTH_SHORT).show();
        }

        // Checks that there is the required information in the bundle passed to this fragment. If not
        // return to the previous screen.
        if (this.listing == null || this.businessId == null || this.category == null) {
            // Todo To be removed
            if (this.category == null) {
                return;
            }
            Toast.makeText(getActivity(), "An unexpected error occurred", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
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
        this.jobPostalCode = this.rootView.findViewById(R.id.jobPostalCode);
        this.businessScore = this.rootView.findViewById(R.id.businessScore);
        this.businessPopularity = this.rootView.findViewById(R.id.businessPopularity);
        this.sendButton = this.rootView.findViewById(R.id.jobSendButton);

        this.jobDate.setFocusable(false);
        ImageView profilePicture = this.rootView.findViewById(R.id.businessImage);
        ProfilePictureHandler.setProfilePicture(profilePicture, this.businessId, requireContext());

        getUserInformation();
        bind();

        JobRequests request = this.getArguments().getParcelable("request");

        if (request != null) {
            this.previousRequest = request;
            this.previousRequestId = this.getArguments().getString("requestId");
            setEditMode();
        }

        return this.rootView;
    }

    @SuppressLint("DefaultLocale")
    private void bind() {

        ImageView backButton = this.rootView.findViewById(R.id.requestBackButton);

        CalendarConstraints.DateValidator validator = DateValidatorPointForward.from(System.currentTimeMillis() - 80000000);
        CalendarConstraints.Builder constraint = new CalendarConstraints.Builder();
        constraint.setValidator(validator);

        this.datePicker = buildDatePicker(false);

        backButton.setOnClickListener(x -> requireActivity().onBackPressed());
        this.sendButton.setOnClickListener(x -> sendRequest());
        this.jobDate.setOnClickListener(x -> setDate());
        this.businessName.setText(this.listing.getName());
        this.businessScore.setText(String.format("%.1f", listing.getReviewScore()));
        this.businessPopularity.setText("(" + this.listing.getNumberOfReviews() + " reviews)");
    }

    private MaterialDatePicker buildDatePicker(boolean hasDateInfo) {

        CalendarConstraints.DateValidator validator = DateValidatorPointForward.from(System.currentTimeMillis() - 80000000);
        CalendarConstraints.Builder constraint = new CalendarConstraints.Builder();
        constraint.setValidator(validator);

        return MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Select date for job")
                .setSelection(hasDateInfo ? this.previousRequest.getDateOfJob().getTime() : System.currentTimeMillis())
                .setCalendarConstraints(constraint.build())
                .build();
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
        if (this.jobPostalCode.length() != 6) {
            this.jobPostalCode.setError("Invalid postal code. Please list your 6 digit " +
                    "postal code number only");
            allCorrect = false;
        }
        if (this.jobDate.getText().toString().equals("Choose a date") || this.date == null) {
            this.jobDate.setError("This field cannot be empty");
            allCorrect = false;
        }
        return allCorrect;
    }

    private void setEditMode() {

        this.jobDescription.setText(this.previousRequest.getJobDescription());
        this.jobDateNote.setText(this.previousRequest.getTimingNote());
        this.date = this.previousRequest.getDateOfJob();
        TextView title = this.rootView.findViewById(R.id.jobRequestTitle);
        title.setText("Edit Job Request");
        this.sendButton.setText("Edit Job Request");
        this.datePicker = buildDatePicker(true);
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        this.jobDate.setText(formatter.format(this.previousRequest.getDateOfJob()));
        String address = this.previousRequest.getAddress();
        String[] addressElements = address.split(", S");
        this.jobAddress.setText(addressElements[0]);
        this.jobPostalCode.setText(addressElements[1]);
        this.jobNumber.setText(this.previousRequest.getPhoneNumber() + "");

    }

    private void sendRequest() {
        if (checkFields() && queryDone) {
            // send request
            String postalCode = this.jobPostalCode.getText().toString();
            String address = this.jobAddress.getText().toString();
            JobRequests requests = new JobRequests(this.userId, this.businessId, this.category);
            requests.setCustomerName(this.username);
            requests.setBusinessName(this.listing.getName());
            requests.setAddress(address + ", S" + postalCode);
            requests.setBusinessPhoneNumber(listing.getPhoneNumber());
            requests.setPhoneNumber(Integer.parseInt(this.jobNumber.getText().toString().trim()));
            requests.setJobDescription(this.jobDescription.getText().toString());
            requests.setDateOfJob(this.date);
            requests.setTimingNote(this.jobDateNote.getText().toString());
            requests.setStatus(JobRequests.STATUS_PENDING);

            DateFormat formatter = new SimpleDateFormat("E, dd MMM");

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(this.previousRequest == null ? getString(R.string.send_job_request_confirmation) :
                            getString(R.string.edit_job_request_confirmation))
                    .setMessage("Job request scheduled for " + formatter.format(this.date))
                    .setPositiveButton(this.previousRequest == null ? "Send request" : "Edit request"
                            , (dialog, which) -> addToDataBase(requests))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    private void addToDataBase(JobRequests requests) {
        CollectionReference dbRequests = FirebaseFirestore.getInstance().collection(JobRequests.DATABASE_COLLECTION);
        if (this.previousRequest != null) { // Edit request
            requests.setDateCreated(this.previousRequest.getDateCreated());
            requests.setId(this.previousRequestId);
            dbRequests.document(this.previousRequestId).set(requests)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getActivity(), "Request edited", Toast.LENGTH_SHORT).show();
                        NotificationHandler.requestChanged(requests);
                        requireActivity().onBackPressed();})
                    .addOnFailureListener(e ->
                            Toast.makeText(getActivity(),
                                    "Failed to edit request", Toast.LENGTH_SHORT).show());
            return;
        }

        String requestId = dbRequests.document().getId();
        requests.setId(requestId);

        dbRequests.document(requestId).set(requests)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Request sent", Toast.LENGTH_SHORT).show();
                    NotificationHandler.requestCreated(requests);
                    requireActivity().onBackPressed();})
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(),
                                "Request failed to send", Toast.LENGTH_SHORT).show());
    }

    private void getUserInformation() {

        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CollectionReference db = FirebaseFirestore.getInstance().collection(User.DATABASE_COLLECTION);

        db.whereEqualTo(FieldPath.documentId(), this.userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                User user = snapshot.toObject(User.class);
                username = user.getUsername();
                phoneNumber = user.getPhoneNumber();
                jobNumber.setText(String.valueOf(phoneNumber));
                address = user.getAddress();
                postalCode = user.getPostalCode();
                jobAddress.setText(address);
                if (postalCode != 0) {
                    jobPostalCode.setText(String.valueOf(postalCode));
                }
                queryDone = true;
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getActivity(), "An unexpected error occurred", Toast.LENGTH_SHORT).show());
    }
}