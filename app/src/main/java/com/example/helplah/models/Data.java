package com.example.helplah.models;

//needed in order to work with the API

public class Data {
    private String Title;
    private String Message;

    public Data(String title, String message) {
        this.Title = title;
        this.Message = message;
    }

    public Data() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

}
