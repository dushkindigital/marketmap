/**
 * Copyright (C) 2011 Dushkin Digital Media, LLC
 * 900 Chapel Street, Ste. 210
 * New Haven, CT 06510-2802
 */
package com.libereco.web.auth;

/**
 * This exception is raised in case we not able to create connection with a
 * Marketplace due to Authorization issue.
 * 
 */
@SuppressWarnings("serial")
public class MarketplaceAuthorizationException extends RuntimeException {

    public MarketplaceAuthorizationException() {
        super();
    }

    public MarketplaceAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarketplaceAuthorizationException(String message) {
        super(message);
    }

    public MarketplaceAuthorizationException(Throwable cause) {
        super(cause);
    }
}
