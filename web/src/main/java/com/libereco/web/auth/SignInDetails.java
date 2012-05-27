/**
 * Copyright (C) 2011 Dushkin Digital Media, LLC
 * 900 Chapel Street, Ste. 210
 * New Haven, CT 06510-2802
 */
package com.libereco.web.auth;

/**
 * @author Aleksandar
 * 
 */
public class SignInDetails {

    private String token;

    private String secretToken;

    private String signInUrl;

    public SignInDetails() {
    }

    public SignInDetails(String token, String secretToken, String signInUrl) {
        this.token = token;
        this.secretToken = secretToken;
        this.signInUrl = signInUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public String getSignInUrl() {
        return signInUrl;
    }

    public void setSignInUrl(String signInUrl) {
        this.signInUrl = signInUrl;
    }

    @Override
    public String toString() {
        return "SignInDetails [token=" + token + ", secretToken="
                + secretToken + ", signInUrl=" + signInUrl + "]";
    }
}
