package com.example.helplah.models;

import java.util.Date;

public class ChatMessage {

    public static final String FIELD_DATE = "date";

    private String senderId;
    private String messageText;
    private boolean isImage;
    private Date date;

    public ChatMessage() {}

    private ChatMessage(String senderId, String messageText, boolean isImage) {
        this.senderId = senderId;
        this.messageText = messageText;
        this.isImage = isImage;
        this.date = new Date();
    }

    public static ChatMessage newTextChat(String senderId, String messageText) {
        return new ChatMessage(senderId, messageText, false);
    }

    public static ChatMessage newImageChat(String senderId, String imageLink) {
        return new ChatMessage(senderId, imageLink, true);
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
