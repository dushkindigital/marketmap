package com.libereco.core.domain;

public enum ListingDuration {

    DAYS_1("Days_1"), DAYS_3("Days_3"), DAYS_5("Days_5"), DAYS_7("Days_7"), DAYS_10("Days_10"), DAYS_14("Days_14"), DAYS_21("Days_21"), DAYS_30(
            "Days_30"), DAYS_60("Days_60"), DAYS_90("Days_90"), DAYS_120("Days_120");

    private String name;

    private ListingDuration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
