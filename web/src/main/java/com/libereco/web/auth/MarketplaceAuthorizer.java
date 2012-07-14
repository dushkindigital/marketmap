package com.libereco.web.auth;

import com.libereco.core.exceptions.ExternalMarketplaceAuthorizationException;

public interface MarketplaceAuthorizer {

    SignInDetails getSignInDetails() throws ExternalMarketplaceAuthorizationException;
}
