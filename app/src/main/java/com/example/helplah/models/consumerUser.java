package com.example.helplah.models;

/**
 * Class for representing a user
 */
public class consumerUser {

    public static final String DATABASE_COLLECTION = "Consumer users";
    public static final String FIELD_ADDRESS = "address";

    private String address;
    private int postalCode;
    private int phoneNumber;

    public consumerUser(String address) {
        this.address = address;
    }

}
