package com.example.helplah.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;
import java.util.function.Consumer;

public class ChatDialogue implements Parcelable {

    public static final String DATABASE_CHANNELS_COLLECTION = "Chats";
    public static final String DATABASE_MESSAGE_COLLECTION = "Messages";
    public static final String PARCELABLE_KEY = "chats";

    public static final String FIELD_ID = "chatId";
    public static final String FIELD_USER_ID = "customerId";
    public static final String FIELD_BUSINESS_ID = "businessId";
    public static final String FIELD_DATE = "time";

    private String customerId;
    private String customerName;
    private String businessName;
    private String businessId;
    private String chatId;
    private long time;

    public ChatDialogue() {}

    public ChatDialogue(String customerId, String customerName,
                        String businessId, String businessName) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.businessId = businessId;
        this.businessName = businessName;
    }

    protected ChatDialogue(Parcel in) {
        customerId = in.readString();
        customerName = in.readString();
        businessName = in.readString();
        businessId = in.readString();
        chatId = in.readString();
    }

    public static final Creator<ChatDialogue> CREATOR = new Creator<ChatDialogue>() {
        @Override
        public ChatDialogue createFromParcel(Parcel in) {
            return new ChatDialogue(in);
        }

        @Override
        public ChatDialogue[] newArray(int size) {
            return new ChatDialogue[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(customerId);
        dest.writeString(customerName);
        dest.writeString(businessName);
        dest.writeString(businessId);
        dest.writeString(chatId);
    }

    // Takes the chat channel and creates one if it does not exist.
    // Then go to the appropriate chat channel chatView.
    public static void goToChatChannel(ChatDialogue channel, Consumer<Bundle> action) {
        CollectionReference chatReference = FirebaseFirestore.getInstance()
                .collection(DATABASE_CHANNELS_COLLECTION);

        chatReference.whereEqualTo(FIELD_USER_ID, channel.getCustomerId())
                .whereEqualTo(FIELD_BUSINESS_ID, channel.getBusinessId())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() == 0) {
                        chatReference.add(channel).addOnSuccessListener(documentReference -> {
                            String id = documentReference.getId();
                            channel.setChatId(id);
                            documentReference.update(FIELD_ID, id, FIELD_DATE, System.currentTimeMillis())
                            .addOnSuccessListener(unused -> {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(PARCELABLE_KEY, channel);
                                action.accept(bundle);
                            });
                        });
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(PARCELABLE_KEY, channel);
                        action.accept(bundle);
                    }
                });
    }

    public static ChatDialogue createChatFromJobRequest(JobRequests request) {
        ChatDialogue chat = new ChatDialogue(request.getCustomerId(), request.getCustomerName(),
                request.getBusinessId(), request.getBusinessName());
        return chat;
    }

    public static Query getLastMessageQuery(ChatDialogue chatDialogue) {
        if (!chatDialogue.isInitialised()) {
            return null;
        }
        return FirebaseFirestore.getInstance()
                .collection(DATABASE_CHANNELS_COLLECTION)
                .document(chatDialogue.getChatId())
                .collection(DATABASE_MESSAGE_COLLECTION)
                .orderBy(ChatMessage.FIELD_DATE, Query.Direction.DESCENDING)
                .limit(1);
    }

    public void sendMessage(String text, boolean business) {
        ChatMessage message = ChatMessage.newTextChat(business ? businessId : customerId, text);
        CollectionReference dbMessages = FirebaseFirestore.getInstance()
                .collection(DATABASE_CHANNELS_COLLECTION).document(this.chatId)
                .collection(DATABASE_MESSAGE_COLLECTION);
        dbMessages.add(message);
    }

    public boolean isInitialised() {
        return this.chatId != null;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatDialogue that = (ChatDialogue) o;
        return Objects.equals(chatId, that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId);
    }
}
