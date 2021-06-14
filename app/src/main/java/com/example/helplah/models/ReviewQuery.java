package com.example.helplah.models;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;

public class ReviewQuery {

    private final FirebaseFirestore firestore;
    private final String listingId;
    private static final ArrayList<String> sortable = new ArrayList<>(Arrays.asList(
            Review.FIELD_SCORE, Review.FIELD_DATE_REVIEWED));

    private String sortBy = null;
    private boolean ascending = false;

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

    public boolean hasSortBy() {
        return this.sortBy != null;
    }

    public boolean isAscending() {
        return ascending;
    }
}
