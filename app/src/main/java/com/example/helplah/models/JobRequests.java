package com.example.helplah.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JobRequests implements Parcelable {

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

    public static final String FIELD_DATE_OF_JOB = "dateOfJob";

    public static final int STATUS_CONFIRMED = 1;
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
    private Date dateCreated;
    private String timingNote;
    private Date dateOfJob;

    public JobRequests() {}

    public JobRequests(String customer, String business, String service) {
        this.customerId = customer;
        this.businessId = business;
        this.service = service;
        this.status = STATUS_PENDING;
        this.dateCreated = new Date(System.currentTimeMillis());
    }

    protected JobRequests(Parcel in) {
        customerId = in.readString();
        businessId = in.readString();
        customerName = in.readString();
        businessName = in.readString();
        service = in.readString();
        jobDescription = in.readString();
        address = in.readString();
        status = in.readInt();
        phoneNumber = in.readInt();
        timingNote = in.readString();
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
        dest.writeInt(status);
        dest.writeInt(phoneNumber);
        dest.writeString(timingNote);
    }

    public static String dateToString(Date date) {
        DateFormat formatter = new SimpleDateFormat("E, dd MMM");
        return formatter.format(date);
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
}
