package com.example.helplah.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
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
    private long time;
    private ArrayList<MessageAuthor> users = new ArrayList<>();
    private String chatId;
    private ChatMessage lastMessage;
    private int unreadCount;
    private boolean isBusiness;

    public ChatDialogue() {}

    public ChatDialogue(String customerId, String customerName,
                        String businessId, String businessName, boolean isBusiness) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.businessId = businessId;
        this.businessName = businessName;
        this.isBusiness = isBusiness;
        this.time = new Date().getTime();
        MessageAuthor customer = new MessageAuthor(customerId, customerName);
        MessageAuthor business = new MessageAuthor(businessId, businessName);
        this.users.add(customer);
        this.users.add(business);
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
                                bundle.putString("id", id);
                                action.accept(bundle);
                            });
                        });
                    } else {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            String id = snapshot.getReference().getId();
                            Bundle bundle = new Bundle();
                            bundle.putString("id", id);
                            action.accept(bundle);
                            return;
                        }
                    }
                });
    }

    public static ChatDialogue createChatFromJobRequest(JobRequests request, Boolean isBusiness) {
        ChatDialogue chat = new ChatDialogue(request.getCustomerId(), request.getCustomerName(),
                request.getBusinessId(), request.getBusinessName(), isBusiness);
        return chat;
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

    public void setUsers(ArrayList<MessageAuthor> users) {
        this.users = users;
    }

    public boolean isBusiness() {
        return isBusiness;
    }

    public void setBusiness(boolean business) {
        isBusiness = business;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
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

    @Override
    public String toString() {
        return "ChatChannel{" +
                "customerName='" + customerName + '\'' +
                ", businessName='" + businessName + '\'' +
                '}';
    }
}
