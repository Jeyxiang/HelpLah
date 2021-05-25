package com.example.helplah.models;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Class to abstract a query made based on this and serach.
 */
public class ListingsQuery {

    private final FirebaseFirestore db;
    
    private int availability = 0;
    private int minNumOfReviews = -1;
    private double minReviewScore = -1;
    private String service = null;
    private String sortBy = null;
    private String language = null;
    private boolean ascending = false;

    public ListingsQuery(FirebaseFirestore db) {
        this.db = db;
    }

    public Query createQuery() {

        Query query = this.db.collection("Businesses");

        if (this.hasService()) {
            query = query.whereArrayContains(Listings.FIELD_SERVICES_LIST, this.service);
        }
        if (this.hasLanguage()) {
            //query = query.w
        }
        if (this.hasAvailability()) {
            query = query.whereEqualTo(Listings.FIELD_AVAILABILITY, this.availability);
        }
        if (this.hasNumberOfReviews()) {
            query = query.whereGreaterThanOrEqualTo(Listings.FIELD_NUMBER_OF_REVIEWS, this.minNumOfReviews);
        }
        if (this.hasMinReviewScore()) {
            query = query.whereGreaterThanOrEqualTo(Listings.FIELD_REVIEW_SCORE, this.minReviewScore);
        } if (this.hasSortBy()) {
            if (this.isAscending()) {
                query = query.orderBy(this.sortBy, Query.Direction.ASCENDING);
            } else {
                query = query.orderBy(this.sortBy, Query.Direction.DESCENDING);
            }
        }
        return query;
    }

    public boolean hasAvailability() {
        return this.availability != 0;
    }

    public boolean hasNumberOfReviews() {
        return this.minNumOfReviews != -1;
    }

    public boolean hasMinReviewScore() {
        return this.minReviewScore != -1d;
    }

    public boolean hasSortBy() {
        return this.sortBy != null;
    }

    public boolean hasService() {
        return this.service != null;
    }

    public boolean hasLanguage() {
        return this.language != null;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public void setMinNumOfReviews(int minNumOfReviews) {
        if (minNumOfReviews >= 0) {
            this.minNumOfReviews = minNumOfReviews;
        }
    }

    public void setMinReviewScore(double minReviewScore) {
        if (minReviewScore >= 0d && minReviewScore <= 5d) {
            this.minReviewScore = minReviewScore;
        }
    }

    public void setService(String service) {
        if (Services.ALLSERVICES.contains(service)) {
            this.service = service;
        }
    }

    public void setSortBy(String field, boolean ascending) {
        if (Listings.sortable.contains(field)) {
            this.sortBy = field;
        }
        if (ascending) {
            this.ascending = true;
        } else {
            this.ascending = false;
        }
    }
    
    public boolean isAscending() {
        return this.ascending;
    }
}
