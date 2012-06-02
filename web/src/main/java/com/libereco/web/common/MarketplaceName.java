package com.libereco.web.common;

import org.apache.commons.lang3.StringUtils;

public enum MarketplaceName {

    EBAY("ebay"), ESTY("esty"), AMAZON("amazon");

    String name;

    private MarketplaceName(String name) {
        this.name = name;
    }

    public static MarketplaceName fromString(String name) {
        if (StringUtils.isNotBlank(name)) {
            for (MarketplaceName marketplaceName : MarketplaceName.values()) {
                if (StringUtils.equals(marketplaceName.name, name)) {
                    return marketplaceName;
                }
            }
        }
        throw new IllegalArgumentException("Invalid name " + name);
    }

    public String getName() {
        return name;
    }
}
