package com.example.helplah.models;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;

public class JobRequestQuery {

    public static final String TAG = "Job request query";
    public static final int LIMIT = 50;
    public static final ArrayList<String> sortable = new ArrayList<>(Arrays.asList(
            JobRequests.FIELD_DATE_CREATED, JobRequests.FIELD_DATE_OF_JOB));

    private final FirebaseFirestore db;

    private String sortBy = null;
    private String id;

    public JobRequestQuery(FirebaseFirestore db, String id) {
        this.db = db;
        this.id = id;
    }

    public Query createQuery() {

        Log.d(TAG, "createQuery: " + this.id + " " + this.sortBy);

        Query query = this.db.collection(JobRequests.DATABASE_COLLECTION);
        query = query.whereEqualTo(JobRequests.FIELD_CUSTOMER_ID, id);

        if (this.hasSortBy()) {
            query = query.orderBy(this.sortBy, Query.Direction.DESCENDING);
        }

        return query.limit(LIMIT);
    }

    public void setSortBy(String field) {
        if (sortable.contains(field)) {
            this.sortBy = field;
        }
    }

    public boolean hasSortBy() {
        return this.sortBy != null;
    }
}
