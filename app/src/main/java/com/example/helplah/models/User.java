package com.example.helplah.models;

public class User {

    // The users will be stored in a document named after their userID in firebase authentication
    // in order to reference between the 2 of them.
    public static final String DATABASE_COLLECTION = "Users";
    public static final String FIELD_ADDRESS = "address";

    private String address;
    boolean isBusiness = false;
    private int postalCode;
    private int phoneNumber;

    public User() {}

    public User(int phoneNumber) { this.phoneNumber = phoneNumber; }

    public User(int phoneNumber, boolean isBusiness) {
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
}

