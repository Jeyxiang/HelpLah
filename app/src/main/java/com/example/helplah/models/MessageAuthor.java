package com.example.helplah.models;

public class MessageAuthor  {

    private String id;
    private String name;
    private String avatar = null;

    public MessageAuthor() {}

    public MessageAuthor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
