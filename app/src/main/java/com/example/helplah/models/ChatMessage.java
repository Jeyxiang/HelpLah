package com.example.helplah.models;

import androidx.annotation.Nullable;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

public class ChatMessage implements IMessage, MessageContentType.Image {

    public static final String FIELD_DATE = "date";
    public static final String FIELD_STATUS = "status";

    public static final int STATUS_UNREAD = 1;
    public static final int STATUS_READ = 2;


    private String messageText;
    private String messageId;
    private String imageURL = null;
    private MessageAuthor user;
    private Date date;
    private int status;

    public ChatMessage() {}

    public ChatMessage(String senderId, String senderName, String messageText) {
        this.user = new MessageAuthor(senderId, senderName);
        this.messageText = messageText;
        this.date = new Date();
        this.user = new MessageAuthor(senderId, senderName);
    }

    @Override
    public String getId() {
        return this.messageId;
    }

    @Override
    public String getText() {
        return this.messageText;
    }

    @Override
    public IUser getUser() {
        return this.user;
    }

    @Override
    public Date getCreatedAt() {
        return this.date;
    }

    @Nullable
    @Override
    public String getImageUrl() {
        return this.imageURL == null ? null : this.imageURL;
    }
}
