package com.example.helplah.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Abstracts a business listing in the app.
 */
public class Listings implements Parcelable {

    // Name of the collection in firebase that stores the business listings
    public static final String DATABASE_COLLECTION = "Businesses";

    // Static fields to query database with
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PRICE = "minPrice";
    public static final String FIELD_REVIEW_SCORE = "reviewScore";
    public static final String FIELD_NUMBER_OF_REVIEWS = "numberOfReviews";
    public static final String FIELD_WEBSITE = "website";
    public static final String FIELD_PHONE_NUMBER = "phoneNumber";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_PRICING_NOTE = "pricingNote";
    public static final String FIELD_SERVICES_LIST = "servicesList.servicesProvided";
    public static final String FIELD_AVAILABILITY = "availability";
    public static final String FIELD_LANGUAGE = "language";

    // Properties which the user can sort on
    public static final ArrayList<String> sortable = new ArrayList<>(Arrays.asList(FIELD_NAME,
            FIELD_PRICE, FIELD_NUMBER_OF_REVIEWS, FIELD_REVIEW_SCORE));


    // Languages spoken by the company TODO
    public static final String LANGUAGE_ENGLISH = "English";
    public static final String LANGUAGE_CHINESE = "Chinese";
    public static final String LANGUAGE_MALAY = "Malay";
    public static final String LANGUAGE_TAMIL = "Tamil";
    public static final ArrayList<String> ALL_LANGUAGES= new ArrayList<>(Arrays.asList(LANGUAGE_ENGLISH,
            LANGUAGE_CHINESE, LANGUAGE_MALAY, LANGUAGE_TAMIL));

    private String name;
    private String description;
    private String pricingNote;
    private String language;
    private double minPrice = -1;
    private boolean isCompany;
    private double reviewScore = 0;
    private int numberOfReviews = 0;
    private int availability;
    private String profilePic;
    private int phoneNumber;
    private String website = "";
    private Services servicesList;
    private HashMap<String, Boolean> languagesSpoken;  // TODO

    // Firestore requires empty constructor to run
    public Listings() {}

    public Listings(String name, String description, String pricingNote, double minPrice,
                    boolean isCompany, double reviewScore, int numberOfReviews, int availability,
                    String profilePic, int phoneNumber, String website, Services servicesList, String language) {
        this.name = name;
        this.description = description;
        this.pricingNote = pricingNote;
        this.minPrice = minPrice;
        this.isCompany = isCompany;
        this.reviewScore = reviewScore;
        this.numberOfReviews = numberOfReviews;
        this.availability = availability;
        this.profilePic = profilePic;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.servicesList = servicesList;
        this.language = language;
    }

    protected Listings(Parcel in) {
        name = in.readString();
        description = in.readString();
        pricingNote = in.readString();
        language = in.readString();
        minPrice = in.readDouble();
        isCompany = in.readByte() != 0;
        reviewScore = in.readDouble();
        numberOfReviews = in.readInt();
        availability = in.readInt();
        profilePic = in.readString();
        phoneNumber = in.readInt();
        website = in.readString();
    }

    public static final Creator<Listings> CREATOR = new Creator<Listings>() {
        @Override
        public Listings createFromParcel(Parcel in) {
            return new Listings(in);
        }

        @Override
        public Listings[] newArray(int size) {
            return new Listings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(pricingNote);
        dest.writeString(language);
        dest.writeDouble(minPrice);
        dest.writeByte((byte) (isCompany ? 1 : 0));
        dest.writeDouble(reviewScore);
        dest.writeInt(numberOfReviews);
        dest.writeInt(availability);
        dest.writeString(profilePic);
        dest.writeInt(phoneNumber);
        dest.writeString(website);
    }

    public Services getServicesList() {
        return servicesList;
    }

    public void setServicesList(Services servicesList) {
        this.servicesList = servicesList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPricingNote() {
        return pricingNote;
    }

    public void setPricingNote(String pricingNote) {
        this.pricingNote = pricingNote;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public boolean isCompany() {
        return isCompany;
    }

    public void setCompany(boolean company) {
        this.isCompany = company;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }

    public double getReviewScore() {
        return reviewScore;
    }

    public void addReview(double newScore) {
        double oldScore = numberOfReviews * reviewScore;
        IncrementNumberOfReviews();
        this.reviewScore = (oldScore + newScore) / getNumberOfReviews();
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void IncrementNumberOfReviews() {
        this.numberOfReviews ++;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setReviewScore(double reviewScore) {
        this.reviewScore = reviewScore;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }
}
