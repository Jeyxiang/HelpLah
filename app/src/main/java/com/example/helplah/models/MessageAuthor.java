package com.example.helplah.models;

import com.stfalcon.chatkit.commons.models.IUser;

public class MessageAuthor implements IUser {

    private String id;
    private String username;
    private String avatar;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public String getAvatar() {
        return null;
    }
}
