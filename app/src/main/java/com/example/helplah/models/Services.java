package com.example.helplah.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Services {

    public static final String SERVICE = "Service";

    public static final String ELECTRICIAN = "Electrician";
    public static final String PLUMBER = "Plumber";
    public static final String CLEANER = "Cleaning";
    public static final String AIRCONDITIONING = "Air-Conditioning";
    public static final String MOVERS = "Movers";
    public static final String LOCKSMITH = "Locksmith";
    public static final String PEST_CONTROL = "Pest Control";

    public static final List<String> ALLSERVICES = new ArrayList<>(Arrays.asList(ELECTRICIAN, PLUMBER,
                                CLEANER, AIRCONDITIONING, MOVERS, LOCKSMITH, PEST_CONTROL));

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

    public ArrayList<String> getServicesProvided() {
        return servicesProvided;
    }

    public void addService(String service) {
        if (ALLSERVICES.contains(service) && !this.servicesProvided.contains(service)) {
            this.servicesProvided.add(service);
        }
    }
}
