package com.example.helplah.adapters;

import com.example.helplah.models.ChatDialogue;
import com.example.helplah.models.ChatChannelLiveData;
import com.example.helplah.models.ChatChannelsViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FirestoreChatChannelRepository implements ChatChannelLiveData.OnLastReachableCallBack,
    ChatChannelLiveData.OnLastVisibleCallBack, ChatChannelsViewModel.ChatChannelRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference chatChannelsRef = db.collection(ChatDialogue.DATABASE_CHANNELS_COLLECTION);
    private Query query = chatChannelsRef.orderBy(ChatDialogue.FIELD_DATE, Query.Direction.DESCENDING).limit(15);
    private DocumentSnapshot lastVisible;
    private boolean isLastReached;

    @Override
    public void setLastVisible(DocumentSnapshot snapshot) {
        this.lastVisible = snapshot;
    }

    @Override
    public void setLastReached(boolean isLastReached) {
        this.isLastReached = isLastReached;
    }

    @Override
    public ChatChannelLiveData getChatChannelLiveDate() {
        if (isLastReached) {
            return null;
        }
        if (this.lastVisible != null) {
            this.query = query.startAfter(this.lastVisible);
        }
        return new ChatChannelLiveData(this.query, this, this);
    }
}
