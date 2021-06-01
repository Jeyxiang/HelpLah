package com.example.helplah.models;

public class User {

    // The users will be stored in a document named after their userID in firebase authentication
    // in order to reference between the 2 of them.
    public static final String DATABASE_COLLECTION = "Users";
    public static final String FIELD_ADDRESS = "address";

    private String address;
    private String username;
    private String email;
    boolean isBusiness = false;
    private int postalCode;
    private int phoneNumber;

    public User() {}

    public User(int phoneNumber) { this.phoneNumber = phoneNumber; }

    public User(String username, String email, int phoneNumber, boolean isBusiness) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isBusiness = isBusiness;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isBusiness() {
        return isBusiness;
    }

    public void setBusiness(boolean business) {
        isBusiness = business;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

