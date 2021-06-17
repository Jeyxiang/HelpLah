package com.example.helplah.models;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ChatChannelLiveData extends LiveData<Operation> implements EventListener<QuerySnapshot> {

    private static final String TAG = "Chat channel live data";

    private final Query query;
    private ListenerRegistration listenerRegistration;
    private final OnLastVisibleCallBack onLastVisibleCallBack;
    private final OnLastReachableCallBack onLastReachableCallBack;

    public ChatChannelLiveData(Query query, OnLastVisibleCallBack lastVisibleCallBack,
                               OnLastReachableCallBack lastReachableCallBack) {
        this.query = query;
        this.onLastVisibleCallBack= lastVisibleCallBack;
        this.onLastReachableCallBack = lastReachableCallBack;
    }

    @Override
    protected void onActive() {
        this.listenerRegistration = query.addSnapshotListener(this);
    }

    @Override
    protected void onInactive() {
        this.listenerRegistration.remove();
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        if (error != null) {
            return;
        }

        for (DocumentChange change : value.getDocumentChanges()) {
            Log.d(TAG, "onEvent: " + change.getType());
            if (change.getType() == DocumentChange.Type.ADDED) {
                Log.d(TAG, "onEvent: item added");
                ChatDialogue chatDialogue = change.getDocument().toObject(ChatDialogue.class);
                setValue(new Operation<>(chatDialogue, Operation.ADDED));
            } else if (change.getType() == DocumentChange.Type.MODIFIED) {
                ChatDialogue chatDialogue = change.getDocument().toObject(ChatDialogue.class);
                setValue(new Operation<>(chatDialogue, Operation.MODIFIED));
            } else if (change.getType() == DocumentChange.Type.REMOVED) {
                ChatDialogue chatDialogue = change.getDocument().toObject(ChatDialogue.class);
                setValue(new Operation<>(chatDialogue, Operation.REMOVED));
            }
        }

        int querySize = value.size();
        if (querySize < 15) {
            this.onLastReachableCallBack.setLastReached(true);
        } else {
            DocumentSnapshot lastVisibleProduct = value.getDocuments().get(querySize - 1);
            this.onLastVisibleCallBack.setLastVisible(lastVisibleProduct);
        }
    }

    public interface OnLastVisibleCallBack {
        void setLastVisible(DocumentSnapshot snapshot);
    }

    public interface OnLastReachableCallBack {
        void setLastReached(boolean isLastReached);
    }
}
