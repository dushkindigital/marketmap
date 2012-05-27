package com.libereco.web.common;

public enum MarketplaceName {

    EBAY("ebay"),ESTY("esty"),AMAZON("amazon");
    
    String name;
    
    private MarketplaceName(String name) {
        this.name =name;
    }
    
}
