package com.libereco.web.auth;

import com.libereco.core.exceptions.ExternalServiceException;

public interface MarketplaceAuthorizer {

    SignInDetails getSignInDetails() throws ExternalServiceException;
}
