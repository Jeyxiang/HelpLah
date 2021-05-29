package com.example.helplah.models;

/**
 * Class for representing a user
 */
public class ConsumerUser {

    // The users will be stored in a document named after their userID in firebase authentication
    // in order to reference between the 2 of them.
    public static final String DATABASE_COLLECTION = "Consumer users";
    public static final String FIELD_ADDRESS = "address";

    private String address;
    private int postalCode;
    private int phoneNumber;

    public ConsumerUser() {}

    public ConsumerUser(int phoneNumber) { this.phoneNumber = phoneNumber; }

}
