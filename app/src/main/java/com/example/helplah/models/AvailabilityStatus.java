package com.example.helplah.models;

/**
 * Static class to represent the current availability of a business.
 */
public class AvailabilityStatus {

    public static final int fourHours = 1;
    public static final int oneDay = 2;
    public static final int twoDays = 3;
    public static final int threeDays = 4;
    public static final int oneWeek = 5;
    public static final int unavailable = 6;

    public static String getAvailabilityText(int status) {
        String message;
        switch (status) {
            case 1:
                message = "In 4 hours";
                break;
            case 2:
                message = "In 24 hours";
                break;
            case 3:
                message = "In 48 hours";
                break;
            case 4:
                message = "In 72 hours";
                break;
            case 5:
                message = "In One week";
                break;
            default:
                message = "Unavailable";
                break;
        }
        return message;
    }
}
