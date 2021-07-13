package com.example.helplah.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class that abstracts the services that a particular business provides to customers.
 */
public class Services implements Parcelable {

    public static final String SERVICE = "Service";

    // The different services currently supported by the app,
    public static final String ELECTRICIAN = "Electrician";
    public static final String PLUMBER = "Plumber";
    public static final String CLEANER = "Cleaning";
    public static final String AIRCONDITIONING = "Air-Conditioning";
    public static final String MOVERS = "Movers";
    public static final String LOCKSMITH = "Locksmith";
    public static final String PEST_CONTROL = "Pest Control";
    public static final String PAINTERS = "Painter";
    public static final String CAR_WASH = "Car Wash";
    public static final String LAUNDRY = "Laundry";

    public static final List<String> ALLSERVICES = new ArrayList<>(Arrays.asList(ELECTRICIAN, PLUMBER,
                                CLEANER, AIRCONDITIONING, MOVERS, LOCKSMITH, PEST_CONTROL, PAINTERS,
                                CAR_WASH, LAUNDRY));

    // Abstracts the services provided by a business
    private ArrayList<String> servicesProvided = new ArrayList<>();

    public Services() {}

    public Services(ArrayList<String> servicesProvided) {
        for (String service : servicesProvided) {
            if (ALLSERVICES.contains(service)) {
                this.servicesProvided.add(service);
            }
        }
    }

    protected Services(Parcel in) {
        servicesProvided = in.createStringArrayList();
    }

    public static final Creator<Services> CREATOR = new Creator<Services>() {
        @Override
        public Services createFromParcel(Parcel in) {
            return new Services(in);
        }

        @Override
        public Services[] newArray(int size) {
            return new Services[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(servicesProvided);
    }

    public ArrayList<String> getServicesProvided() {
        return servicesProvided;
    }

    public void addService(String service) {
        if (ALLSERVICES.contains(service) && !this.servicesProvided.contains(service)) {
            this.servicesProvided.add(service);
        }
    }
}
