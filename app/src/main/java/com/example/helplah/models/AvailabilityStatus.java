package com.example.helplah.models;

public class AvailabilityStatus {

    private static final int fourHours = 1;
    private static final int OneDay = 2;
    private static final int TwoDay = 3;
    private static final int ThreeDays = 4;
    private static final int OneWeek = 5;
    private static final int Unavailable = 6;

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
