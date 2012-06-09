package com.libereco.core.domain;

import javax.persistence.Embeddable;

@Embeddable
public class ItemLocation {

    private String itemLocation;
    private String postalCode;
    
    
    public ItemLocation() {
        // TODO Auto-generated constructor stub
    }
    
    public ItemLocation(String itemLocation, String postalCode) {
        this.itemLocation = itemLocation;
        this.postalCode = postalCode;
    }
    
    public String getItemLocation() {
        return itemLocation;
    }
    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    
}
