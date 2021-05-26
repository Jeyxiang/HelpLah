package com.example.helplah.models;

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
                message = "4 hours";
                break;
            case 2:
                message = "24 hours";
                break;
            case 3:
                message = "48 hours";
                break;
            case 4:
                message = "72 hours";
                break;
            case 5:
                message = "One week";
                break;
            default:
                message = "Unavailable";
                break;
        }
        return message;
    }
}
