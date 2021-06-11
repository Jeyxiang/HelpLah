package com.example.helplah.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class JobRequestQuery {

    public static final String TAG = "Job request query";
    public static final int LIMIT = 50;
    public static final ArrayList<String> sortable = new ArrayList<>(Arrays.asList(
            JobRequests.FIELD_DATE_CREATED, JobRequests.FIELD_DATE_OF_JOB));

    private final FirebaseFirestore db;

    private String sortBy = null;
    private String id;
    private ArrayList<Integer> statusList = new ArrayList<>();
    private Date start;
    private Date end;
    private boolean isBusiness;
    private boolean ascending = false;
    private boolean isEmpty = true;

    public JobRequestQuery(FirebaseFirestore db, String id, boolean isBusiness) {
        this.db = db;
        this.id = id;
        this.isBusiness = isBusiness;
    }

    public Query createQuery() {

        Log.d(TAG, "createQuery: " + this.id + " " + this.sortBy);

        Query query = this.db.collection(JobRequests.DATABASE_COLLECTION);

        if (isBusiness) {
            query = query.whereEqualTo(JobRequests.FIELD_BUSINESS_ID, this.id);
            query = query.whereEqualTo(JobRequests.FIELD_REMOVED, false);
        } else {
            query = query.whereEqualTo(JobRequests.FIELD_CUSTOMER_ID, this.id);
        }

        if (this.hasStatus()) {
            Log.d(TAG, "createQuery: " +statusList.toString());
            query = query.whereIn(JobRequests.FIELD_STATUS, this.statusList);
        }

        if (this.hasDate() && this.sortBy == JobRequests.FIELD_DATE_OF_JOB) {
            query = query.whereGreaterThanOrEqualTo(JobRequests.FIELD_DATE_OF_JOB, this.start);
            query = query.whereLessThan(JobRequests.FIELD_DATE_OF_JOB, this.end);
        }
        if (this.hasSortBy()) {
            if (this.ascending) {
                query = query.orderBy(this.sortBy, Query.Direction.ASCENDING);
            } else {
                query = query.orderBy(this.sortBy, Query.Direction.DESCENDING);
            }
        }

        return query.limit(LIMIT);
    }

    public void setSortBy(String field, boolean ascending) {
        if (sortable.contains(field)) {
            this.sortBy = field;
            this.ascending = ascending;
        }
        this.isEmpty = false;
    }

    public void setStatus(ArrayList<Integer> status) {
        this.statusList = status;
        this.isEmpty = false;
    }

    public void setDateFilter(@NonNull Date start, @NonNull Date end) {
        this.start = Date.from(start.toInstant().truncatedTo(ChronoUnit.DAYS));
        this.end = Date.from(end.toInstant().truncatedTo(ChronoUnit.DAYS));
        this.isEmpty = false;
    }

    public boolean hasSortBy() {
        return this.sortBy != null;
    }

    public boolean hasStatus() {
        return !this.statusList.isEmpty();
    }

    public boolean hasDate() {
        return this.start != null && this.end != null;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
