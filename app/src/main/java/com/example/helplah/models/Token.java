package com.example.helplah.models;

//Token is the unique ID of devices
public class Token {

    private String token;

    public static String DATABASE_COLLECTION = "Tokens";

    public Token(String token) {
        this.token = token;
    }

    public Token() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

