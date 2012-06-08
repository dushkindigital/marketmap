package com.libereco.core.domain;

public enum ListingDuration {

    DAYS_3("Days_3"), DAYS_5("Days_5"), DAYS_7("Days_7"), DAYS_10("Days_10"), DAYS_30("Days_30");

    private String name;

    private ListingDuration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
