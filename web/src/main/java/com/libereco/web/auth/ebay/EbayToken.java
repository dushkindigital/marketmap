/**
 * Copyright (C) 2011 Dushkin Digital Media, LLC
 * 900 Chapel Street, Ste. 210
 * New Haven, CT 06510-2802
 */
package com.libereco.web.auth.ebay;

import java.util.Calendar;

/**
 * @author Aleksandar
 * 
 */
public class EbayToken {

    private String token;

    private Calendar expirationTime;

    public EbayToken(String token, Calendar expirationTime) {
        super();
        this.token = token;
        this.expirationTime = expirationTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Calendar getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Calendar expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public String toString() {
        return "EbayToken [token=" + token + ", expirationTime="
                + expirationTime + "]";
    }
}
