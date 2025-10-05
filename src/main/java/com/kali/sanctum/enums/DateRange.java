package com.kali.sanctum.enums;

public enum DateRange {
    WEEKLY,
    MONTHLY,
    YEARLY;

    public static DateRange from(String value) {
        if (value == null)
            throw new IllegalArgumentException();

        switch ((value.trim().toLowerCase())) {
            case "weekly":
                return WEEKLY;
            case "monthly":
                return MONTHLY;
            case "yearly":
                return YEARLY;
            default:
                throw new IllegalArgumentException(
                        "Invalid DateRange: " + value + ". Valid values: weekly, monthly, yearly");
        }
    }
}
