package com.example.helplah.adapters;

import android.util.Log;

import com.example.helplah.models.ChatDialogue;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

public class ChatDialogueDataSource {

    private static final String TAG = "Chat dialogue data source";

    public interface DataSourceListener {
        void chatDialogueAdded(ChatDialogue chatDialogue);

        void chatDialogueModified(ChatDialogue chatDialogue);

        void chatDialogueRemoved(ChatDialogue chatDialogue);
    }

    private Query query;
    private final DataSourceListener dataSourceListener;
    private ListenerRegistration listenerRegistration;

    public ChatDialogueDataSource(DataSourceListener listener) {
        this.dataSourceListener = listener;

        this.query = FirebaseFirestore.getInstance().collection(ChatDialogue.DATABASE_CHANNELS_COLLECTION)
                .orderBy(ChatDialogue.FIELD_DATE, Query.Direction.DESCENDING);
    }

    private void addSnapshot(Query query) {
        Log.d(TAG, "addSnapshot: Adding snapshot");
        this.listenerRegistration = query.addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            assert value != null;
            for (DocumentChange change : value.getDocumentChanges()) {
                ChatDialogue chatDialogue = change.getDocument().toObject(ChatDialogue.class);
                if (change.getType() == DocumentChange.Type.ADDED) {
                    dataSourceListener.chatDialogueAdded(chatDialogue);
                } else if (change.getType() == DocumentChange.Type.MODIFIED) {
                    dataSourceListener.chatDialogueModified(chatDialogue);
                } else if (change.getType() == DocumentChange.Type.REMOVED) {
                    dataSourceListener.chatDialogueRemoved(chatDialogue);
                }
            }
        });
    }

    public void startListening() {
        addSnapshot(this.query);
    }

    public void stopListening() {
        if (this.listenerRegistration != null) {
            Log.d(TAG, "stopListening: Stopping listening");
            this.listenerRegistration.remove();
        }
    }
}
