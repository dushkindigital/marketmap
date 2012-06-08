package com.libereco.core.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public enum ListingCondition {

    NEW("New"),
    NEW_OTHER("New Other"),
    NEW_WITH_DEFECTS("New with Defects"),
    MANUFACTURER_REFURBISHED("Manufacturer Refurbished"),
    SELLER_REFURBISHED("Seller refurbished"),
    LIKE_NEW("Like New"),
    USED("Used"),
    VERY_GOOD("Very Good"),
    GOOD("Good"),
    ACCEPTABLE("Acceptable"),
    PARTS_NOT_WORKING("Parts not working");

    private String message;

    private ListingCondition(String message) {
        this.message = message;
    }

    public static List<String> messages() {
        List<String> messages = new ArrayList<String>();
        ListingCondition[] values = ListingCondition.values();
        for (ListingCondition listingCondition : values) {
            messages.add(listingCondition.message);
        }
        return messages;
    }

    public static ListingCondition fromMessage(String message) {
        ListingCondition[] values = ListingCondition.values();

        for (ListingCondition listingCondition : values) {
            if (StringUtils.equals(listingCondition.message, message)) {
                return listingCondition;
            }
        }
        throw new IllegalArgumentException("No ListingCondition found for message : " + message);
    }
}
