package com.example.helplah.models;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

/**
 * Class to construct a firestore query made based on the different filters and sort.
 */
public class ListingsQuery {

    public static final String TAG = "ListingsQuery";

    private final FirebaseFirestore db;

    // Fields to query on
    private ArrayList<Integer> availability = new ArrayList<>();
    private String service = null;
    private String sortBy = null;
    private String preferredLanguage = null; // TODO
    private boolean ascending = false;
    private boolean isEmpty = true;

    public ListingsQuery(FirebaseFirestore db) {
        this.db = db;
    }

    public Query createQuery() {

        Query query = this.db.collection(Listings.DATABASE_COLLECTION);

        if (this.hasService()) {
            query = query.whereArrayContains(Listings.FIELD_SERVICES_LIST, this.service);
        }
        if (this.hasPreferredLanguage()) {
            // TODO
        }
        if (this.hasAvailability()) {
            query = query.whereIn(Listings.FIELD_AVAILABILITY, this.availability);
        }
        if (this.hasSortBy()) {
            if (this.isAscending()) {
                query = query.orderBy(this.sortBy, Query.Direction.ASCENDING);
            } else {
                query = query.orderBy(this.sortBy, Query.Direction.DESCENDING);
            }
        }
        return query;
    }

    public boolean hasAvailability() {
        return !this.availability.isEmpty();
    }

    public boolean hasSortBy() {
        return this.sortBy != null;
    }

    public boolean hasService() {
        return this.service != null;
    }

    public boolean hasPreferredLanguage() {
        return this.preferredLanguage != null;
    }

    // Set the query to search based on certain availability
    public void setAvailability(ArrayList<Integer> availability) {
        this.availability = availability;
        this.isEmpty = false;
    }

    // Set the query to search based on the type of service provided
    public void setService(String service) {
        if (Services.ALLSERVICES.contains(service)) {
            this.service = service;
        }
        this.isEmpty = false;
    }

    // Set the query to sort by a field as listed in Listings.sortable
    public void setSortBy(String field, boolean ascending) {
        if (Listings.sortable.contains(field)) {
            this.sortBy = field;
            this.ascending = ascending;
        }
        this.isEmpty = false;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        if (preferredLanguage != null && Listings.ALL_LANGUAGES.contains(preferredLanguage)) {
            this.preferredLanguage = preferredLanguage;
        }
    }
    
    public boolean isAscending() {
        return this.ascending;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }
}
