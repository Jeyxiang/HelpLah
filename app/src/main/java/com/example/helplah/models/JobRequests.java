package com.example.helplah.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class JobRequests implements Parcelable {

    private static final String TAG = "Job requests";
    public static final String DATABASE_COLLECTION = "Job requests";
    public static final String FIELD_CUSTOMER_ID = "customerId";
    public static final String FIELD_BUSINESS_ID = "businessId";
    public static final String FIELD_CUSTOMER_NAME = "customerName";
    public static final String FIELD_BUSINESS_NAME = "businessName";
    public static final String FIELD_SERVICE = "service";
    public static final String FIELD_JOB_DESCRIPTION = "jobDescription";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_CONFIRMED_TIMING = "confirmedTiming";
    public static final String FIELD_DECLINE_MESSAGE = "declineMessage";
    public static final String FIELD_PHONE_NUMBER = "phoneNumber";
    public static final String FIELD_DATE_CREATED = "dateCreated";
    public static final String FIELD_DATE_TIMING_NOTE = "timingNote";
    public static final String FIELD_REMOVED = "removed";
    public static final String FIELD_USER_REMOVED = "userRemoved";
    public static final String FIELD_REVIEWED = "reviewed";
    public static final String FIELD_DATE_OF_JOB = "dateOfJob";
    public static final String FIELD_ID = "id";

    public static final int STATUS_CONFIRMED = 1;
    public static final int STATUS_FINISHED = 3;
    public static final int STATUS_CANCELLED = 2;
    public static final int STATUS_PENDING = 0;

    private String customerId;
    private String businessId;
    private String customerName;
    private String businessName;
    private String service;
    private String jobDescription;
    private String address;
    private String confirmedTiming;
    private String declineMessage;
    private int status;
    private int phoneNumber;
    private int businessPhoneNumber;
    private Date dateCreated;
    private String timingNote;
    private Date dateOfJob;
    private boolean removed = false;
    private boolean userRemoved = false;
    private boolean reviewed = false;
    private String id;

    public JobRequests() {}

    public JobRequests(String customer, String business, String service) {
        this.customerId = customer;
        this.businessId = business;
        this.service = service;
        this.status = STATUS_PENDING;
        this.dateCreated = new Date(System.currentTimeMillis());
    }

    public JobRequests(String businessName, String customerName, long dateOfJob, String id) {
        this.businessName = businessName;
        this.customerName = customerName;
        this.dateOfJob = new Date(dateOfJob);
        this.id = id;
    }

    protected JobRequests(Parcel in) {
        customerId = in.readString();
        businessId = in.readString();
        customerName = in.readString();
        businessName = in.readString();
        service = in.readString();
        jobDescription = in.readString();
        address = in.readString();
        confirmedTiming = in.readString();
        declineMessage = in.readString();
        status = in.readInt();
        phoneNumber = in.readInt();
        businessPhoneNumber = in.readInt();
        timingNote = in.readString();
        removed = in.readByte() != 0;
        userRemoved = in.readByte() != 0;
        reviewed = in.readByte() != 0;
        dateOfJob = new Date(in.readLong());
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(customerId);
        dest.writeString(businessId);
        dest.writeString(customerName);
        dest.writeString(businessName);
        dest.writeString(service);
        dest.writeString(jobDescription);
        dest.writeString(address);
        dest.writeString(confirmedTiming);
        dest.writeString(declineMessage);
        dest.writeInt(status);
        dest.writeInt(phoneNumber);
        dest.writeInt(businessPhoneNumber);
        dest.writeString(timingNote);
        dest.writeByte((byte) (removed ? 1 : 0));
        dest.writeByte((byte) (userRemoved ? 1 : 0));
        dest.writeByte((byte) (reviewed ? 1 : 0));
        dest.writeLong(dateOfJob.getTime());
        dest.writeString(id);
    }

    public static final Creator<JobRequests> CREATOR = new Creator<JobRequests>() {
        @Override
        public JobRequests createFromParcel(Parcel in) {
            return new JobRequests(in);
        }

        @Override
        public JobRequests[] newArray(int size) {
            return new JobRequests[size];
        }
    };

    public static String dateToString(Date date) {
        DateFormat formatter = new SimpleDateFormat("E, dd MMM");
        return formatter.format(date);
    }

    public static boolean isJobOver(JobRequests request) {
        long current_time = System.currentTimeMillis();
        return current_time > request.getDateOfJob().getTime();
    }

    public static void markAsReviewed(String requestId) {
        CollectionReference db = FirebaseFirestore.getInstance().collection(DATABASE_COLLECTION);
        db.document(requestId).update(FIELD_REVIEWED, true);
    }

    public static void goToAddress(JobRequests requests, Context context) {
        String address = requests.getAddress();
        String[] addressElements = address.split(", S");
        Uri intentUri = Uri.parse("geo:1.3521,103.8198?q=" + addressElements[1] + ", Singapore");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            context.startActivity(mapIntent);
        } catch (Exception e) {
            Log.d(TAG, "goToAddress: An error occurred " + e.getMessage());
            Toast.makeText(context, "Unable to open google maps", Toast.LENGTH_SHORT).show();
        }
    }

    public static void goToCalendar(JobRequests requests, Context context) {
        if (requests.getStatus() != JobRequests.STATUS_CONFIRMED || requests.getConfirmedTiming() == null) {
            Toast.makeText(context, "You can only add the job request to your calendar " +
                    "once it has been confirmed.", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar dayOfJob = Calendar.getInstance();
        dayOfJob.setTime(requests.getDateOfJob());
        int year = dayOfJob.get(Calendar.YEAR);
        int month = dayOfJob.get(Calendar.MONTH);
        int day = dayOfJob.get(Calendar.DAY_OF_MONTH);
        String[] confirmedTime = requests.getConfirmedTiming().split(":");
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day, Integer.parseInt(confirmedTime[0]), Integer.parseInt(confirmedTime[1]));
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, beginTime.getTimeInMillis() + 3600000)
                .putExtra(CalendarContract.Events.TITLE, "Job request")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Job request with " + requests.getBusinessName())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, requests.getAddress());
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Unable to open calendar", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "goToCalendar: An error occurred " + e.getMessage());
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getBusinessPhoneNumber() {
        return businessPhoneNumber;
    }

    public void setBusinessPhoneNumber(int businessPhoneNumber) {
        this.businessPhoneNumber = businessPhoneNumber;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTimingNote() {
        return timingNote;
    }

    public void setTimingNote(String timingNote) {
        this.timingNote = timingNote;
    }

    public Date getDateOfJob() {
        return dateOfJob;
    }

    public void setDateOfJob(Date dateOfJob) {
        this.dateOfJob = dateOfJob;
    }

    public String getConfirmedTiming() {
        return confirmedTiming;
    }

    public void setConfirmedTiming(String confirmedTiming) {
        this.confirmedTiming = confirmedTiming;
    }

    public String getDeclineMessage() {
        return declineMessage;
    }

    public void setDeclineMessage(String declineMessage) {
        this.declineMessage = declineMessage;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean isUserRemoved() {
        return userRemoved;
    }

    public void setUserRemoved(boolean userRemoved) {
        this.userRemoved = userRemoved;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobRequests requests = (JobRequests) o;
        return Objects.equals(id, requests.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
