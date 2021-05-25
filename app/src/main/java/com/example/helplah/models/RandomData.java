package com.example.helplah.models;

import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.util.ArrayList;

// Populates the database with random Businesses
public class RandomData {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static void fillDatabase(int numberOfItems) {
        for (int i = 0 ; i < numberOfItems; i++) {
            FirebaseFirestore.getInstance().collection("Businesses").add(randomListing());
        }
    }

    public static Listings randomListing() {
        String name = randomString(8);
        String description = randomString(100);
        String pricingNote = randomString(30);
        double minPrice = randomDouble(10, 100);
        boolean isCompany = true;
        double reviewScore = randomDouble(4.9);
        int numberOfReviews = randomInt(100);
        int availability = randomInt(1, 5);
        String profilePic = "www.google.com";
        int phoneNumber = randomInt(10000000, 99999999);
        String website = "www.plumber.com";
        ArrayList<String> service = new ArrayList<>();
        service.add(Services.ELECTRICIAN);
        Services servicesProvided = new Services(service);
        return new Listings(name + " company", description, pricingNote, minPrice, isCompany,
                reviewScore, numberOfReviews, availability, profilePic, phoneNumber, website,
                servicesProvided, Listings.LANGUAGE_ENGLISH);
    }

    public static String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static double randomDouble(double max) {
        return Math.random() * max;
    }

    public static double randomDouble(double min , double max) {
        return Math.random() * (max - min);
    }

    public static int randomInt(int max) {
        return (int) (Math.random() * max);
    }

    public static int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min));
    }
}
