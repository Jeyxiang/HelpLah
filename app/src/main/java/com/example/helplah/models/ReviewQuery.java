package com.example.helplah.models;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class responsible for creating a firestore query to get the reviews of a particular business
 * based on different sorts and filters.
 */
public class ReviewQuery {

    public static final String TAG = "Review query";

    private final FirebaseFirestore firestore;
    private final String listingId;
    private static final ArrayList<String> sortable = new ArrayList<>(Arrays.asList(
            Review.FIELD_SCORE, Review.FIELD_DATE_REVIEWED));

    private String sortBy = null;
    private boolean ascending = false;
    private String service;
    private int rating = -1;

    public ReviewQuery(FirebaseFirestore firebaseFirestore, String listingId) {
        this.firestore = firebaseFirestore;
        this.listingId = listingId;
    }

    public static Query getPreview(FirebaseFirestore firebaseFirestore, String listingId) {
        ReviewQuery query = new ReviewQuery(firebaseFirestore, listingId);
        query.setSortBy(Review.FIELD_DATE_REVIEWED, false);
        return query.createQuery().limit(3);
    }

    public Query createQuery() {
        Query query = this.firestore.collection(Listings.DATABASE_COLLECTION)
                        .document(this.listingId).collection(Review.DATABASE_COLLECTION);

        if (this.hasService()) {
            query = query.whereEqualTo(Review.FIELD_SERVICE, this.service);
        }
        if (this.hasRating()) {
            query = query.whereEqualTo(Review.FIELD_SCORE, this.rating);
        }

        if (this.hasSortBy()) {
            if (this.ascending) {
                query = query.orderBy(this.sortBy, Query.Direction.ASCENDING);
            } else {
                query = query.orderBy(this.sortBy, Query.Direction.DESCENDING);
            }
        }

        return query;
    }

    public void setSortBy(String field, boolean ascending) {
        if (sortable.contains(field)) {
            this.sortBy = field;
            this.ascending = ascending;
        }
    }

    public void setRating(int rating) {
        if (rating > 0 && rating <= 5) {
            this.rating = rating;
        } else {
            this.rating = -1;
        }
    }

    public void setService(String service) {
        if (Services.ALLSERVICES.contains(service)) {
            this.service = service;
        }
    }

    public boolean hasSortBy() {
        return this.sortBy != null;
    }

    public boolean hasRating() {
        return this.rating != -1;
    }

    public boolean hasService() {
        return this.service != null;
    }

    public boolean isAscending() {
        return ascending;
    }
}
