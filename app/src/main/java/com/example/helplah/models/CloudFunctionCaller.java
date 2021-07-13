package com.example.helplah.models;

import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for calling the https firebase cloud function.
 */
public class CloudFunctionCaller {

    private static final String TAG = "Cloud function caller";

    /**
     * It is called when the contact number of a user is changed. The cloud functions then makes the
     * necessary changes in the database.
     * @param id The user id of the user.
     * @param newNumber The new number.
     * @param isBusiness Is the user who made the change a business user or consumer user.
     */
    public static void contactChanged(String id, int newNumber, boolean isBusiness) {
        Map<String, Object> data = new HashMap<>();
        data.put("newNumber", newNumber);
        data.put("id", id);
        data.put("isBusiness", isBusiness);

        FirebaseFunctions.getInstance()
                .getHttpsCallable("contactNumberChanged")
                .call(data)
                .continueWith((Continuation<HttpsCallableResult, Object>) task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Successfully updated contact number");
                        return (String) task.getResult().getData();
                    } else {
                        Log.d(TAG, "Failed to update contact number");
                        return null;
                    }
                });
    }

    /**
     * It is called when a business changes its business name. The cloud function then makes the
     * necessary changes in the database.
     * @param listingId The id of the listing whose name was changed.
     * @param newName The new name.
     */
    public static void listingNameChanged(String listingId, String newName) {
        Map<String, Object> data = new HashMap<>();
        data.put("listingId", listingId);
        data.put("newName", newName);

        FirebaseFunctions.getInstance()
                .getHttpsCallable("listingNameChanged")
                .call(data)
                .continueWith((Continuation<HttpsCallableResult, Object>) task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Successfully updated listing name");
                    } else {
                        Log.d(TAG, "Failed to update listing name");
                    }
                    return null;
                });
    }
}
