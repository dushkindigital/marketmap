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
// TODO: Placeholder for common marketplace authorization methods, if we can
// identify them once a closer look at authorization for different markeplaces
// is performed
public interface MarketplaceAuthorizer {

    SignInDetails getSignInDetails() throws MarketplaceAuthorizationException;
}
